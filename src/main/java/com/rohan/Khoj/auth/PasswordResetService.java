package com.rohan.Khoj.auth;

import com.rohan.Khoj.clinic.ClinicEntity;
import com.rohan.Khoj.clinic.ClinicRepository;
import com.rohan.Khoj.common.UserType;
import com.rohan.Khoj.doctor.DoctorEntity;
import com.rohan.Khoj.doctor.DoctorRepository;
import com.rohan.Khoj.exception.BadRequestException;
import com.rohan.Khoj.patient.PatientEntity;
import com.rohan.Khoj.patient.PatientRepository;
import com.rohan.Khoj.security.RefreshTokenService;
import com.rohan.Khoj.notification.OtpNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final OtpNotificationService otpNotificationService;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int OTP_VALIDITY_MINUTES = 10;
    private static final int MAX_OTP_ATTEMPTS = 5;

    private static class MatchedUser {
        final UUID userId;
        final UserType userType;
        final String email;

        MatchedUser(UUID userId, UserType userType, String email) {
            this.userId = userId;
            this.userType = userType;
            this.email = email;
        }
    }

    /**
     * Initiates the forgot password request.
     * Checks if email and primary mobile match across Patient, Doctor, or Clinic.
     * Generates a 6-digit OTP valid for 10 minutes and logs/delivers it.
     */
    @Transactional
    public void initiateForgotPassword(ForgotPasswordRequestDTO request) {
        String email = request.getEmail().trim().toLowerCase();
        String primaryMobile = request.getPrimaryMobile().trim();

        MatchedUser matchedUser = findUserByEmailAndMobile(email, primaryMobile);

        if (matchedUser == null) {
            log.warn("Forgot password attempt: No match found for email: {} and mobile ending in {}", 
                    email, primaryMobile.length() >= 4 ? primaryMobile.substring(primaryMobile.length() - 4) : "****");
            // Security best practice: don't reveal whether user exists
            return;
        }

        // Invalidate any existing unused OTPs for this email
        passwordResetTokenRepository.invalidateActiveTokensForEmail(email);

        // Generate 6-digit secure numeric OTP (100000 - 999999)
        String otp = String.format("%06d", 100000 + SECURE_RANDOM.nextInt(900000));

        PasswordResetTokenEntity tokenEntity = PasswordResetTokenEntity.builder()
                .userId(matchedUser.userId)
                .userType(matchedUser.userType)
                .email(email)
                .otp(otp)
                .expiryDate(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES))
                .used(false)
                .attemptCount(0)
                .build();

        passwordResetTokenRepository.save(tokenEntity);

        // Deliver OTP via configured Email and SMS services (with specified sender IDs)
        otpNotificationService.sendEmailOtp(email, otp);
        otpNotificationService.sendSmsOtp(primaryMobile, otp);

        log.info("==========================================================");
        log.info("[PASSWORD RESET OTP DISPATCHED] For User: {} ({})", email, matchedUser.userType);
        log.info("[PASSWORD RESET OTP DISPATCHED] OTP CODE: {}", otp);
        log.info("[PASSWORD RESET OTP DISPATCHED] Valid for {} minutes", OTP_VALIDITY_MINUTES);
        log.info("==========================================================");
    }

    /**
     * Resets the password using the OTP.
     * Updates the entity password, marks OTP as used, and revokes all active refresh tokens.
     */
    @Transactional
    public void resetPassword(ResetPasswordRequestDTO request) {
        String email = request.getEmail().trim().toLowerCase();
        String rawOtp = request.getOtp().trim();

        PasswordResetTokenEntity tokenEntity = passwordResetTokenRepository
                .findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new BadRequestException("No active password reset request found for this email."));

        if (tokenEntity.isExpired()) {
            tokenEntity.setUsed(true);
            passwordResetTokenRepository.save(tokenEntity);
            throw new BadRequestException("OTP has expired. Please request a new OTP.");
        }

        if (tokenEntity.getAttemptCount() >= MAX_OTP_ATTEMPTS) {
            tokenEntity.setUsed(true);
            passwordResetTokenRepository.save(tokenEntity);
            throw new BadRequestException("Maximum verification attempts exceeded. Please request a new OTP.");
        }

        if (!tokenEntity.getOtp().equals(rawOtp)) {
            tokenEntity.setAttemptCount(tokenEntity.getAttemptCount() + 1);
            passwordResetTokenRepository.save(tokenEntity);
            int remaining = MAX_OTP_ATTEMPTS - tokenEntity.getAttemptCount();
            throw new BadRequestException("Invalid OTP. " + remaining + " attempts remaining.");
        }

        // OTP is valid - encode new password
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());

        switch (tokenEntity.getUserType()) {
            case PATIENT -> {
                PatientEntity patient = patientRepository.findById(tokenEntity.getUserId())
                        .orElseThrow(() -> new BadRequestException("Patient account not found."));
                patient.setPassword(encodedPassword);
                patient.setUpdatedAt(LocalDateTime.now());
                patientRepository.save(patient);
            }
            case DOCTOR -> {
                DoctorEntity doctor = doctorRepository.findById(tokenEntity.getUserId())
                        .orElseThrow(() -> new BadRequestException("Doctor account not found."));
                doctor.setPassword(encodedPassword);
                doctor.setUpdatedAt(LocalDateTime.now());
                doctorRepository.save(doctor);
            }
            case CLINIC -> {
                ClinicEntity clinic = clinicRepository.findById(tokenEntity.getUserId())
                        .orElseThrow(() -> new BadRequestException("Clinic account not found."));
                clinic.setPassword(encodedPassword);
                clinic.setUpdatedAt(LocalDateTime.now());
                clinicRepository.save(clinic);
            }
        }

        // Mark OTP token as used
        tokenEntity.setUsed(true);
        passwordResetTokenRepository.save(tokenEntity);

        // Security best practice: Revoke all active refresh tokens to force re-login on all devices
        refreshTokenService.revokeAllUserTokens(tokenEntity.getUserId());

        log.info("Password reset successfully for user {} ({})", email, tokenEntity.getUserType());
    }

    private MatchedUser findUserByEmailAndMobile(String email, String mobile) {
        Optional<PatientEntity> patient = patientRepository.findByEmailId(email);
        if (patient.isPresent() && mobile.equals(patient.get().getPrimaryMobile())) {
            return new MatchedUser(patient.get().getId(), UserType.PATIENT, email);
        }

        Optional<DoctorEntity> doctor = doctorRepository.findByEmailId(email);
        if (doctor.isPresent() && mobile.equals(doctor.get().getPrimaryMobile())) {
            return new MatchedUser(doctor.get().getId(), UserType.DOCTOR, email);
        }

        Optional<ClinicEntity> clinic = clinicRepository.findByEmailId(email);
        if (clinic.isPresent() && mobile.equals(clinic.get().getPrimaryMobile())) {
            return new MatchedUser(clinic.get().getId(), UserType.CLINIC, email);
        }

        return null;
    }
}
