package com.rohan.Khoj.auth;

import com.rohan.Khoj.clinic.ClinicEntity;
import com.rohan.Khoj.clinic.ClinicRepository;
import com.rohan.Khoj.common.UserType;
import com.rohan.Khoj.doctor.DoctorEntity;
import com.rohan.Khoj.doctor.DoctorRepository;
import com.rohan.Khoj.exception.BadRequestException;
import com.rohan.Khoj.notification.OtpNotificationService;
import com.rohan.Khoj.patient.PatientEntity;
import com.rohan.Khoj.patient.PatientRepository;
import com.rohan.Khoj.security.RefreshTokenService;
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
     * Verifies that the email exists and the primary mobile number matches.
     * Logs detailed internal diagnostic information if there is a mismatch.
     * Throws BadRequestException with an informative message if unmatched so the UI can display it.
     */
    @Transactional
    public void initiateForgotPassword(ForgotPasswordRequestDTO request) {
        String email = request.getEmail().trim().toLowerCase();
        String primaryMobile = request.getPrimaryMobile().trim();

        // 1. Check if email exists in any role
        Optional<PatientEntity> patientByEmail = patientRepository.findByEmailId(email);
        Optional<DoctorEntity> doctorByEmail = doctorRepository.findByEmailId(email);
        Optional<ClinicEntity> clinicByEmail = clinicRepository.findByEmailId(email);

        boolean emailExists = patientByEmail.isPresent() || doctorByEmail.isPresent() || clinicByEmail.isPresent();

        if (!emailExists) {
            log.warn("[FORGOT PASSWORD FAILURE] Email not registered in system: '{}'", email);
            throw new BadRequestException("No account found matching this email and mobile number.");
        }

        // 2. Check for exact match with primary mobile
        MatchedUser matchedUser = null;
        String registeredMobile = null;

        if (patientByEmail.isPresent()) {
            registeredMobile = patientByEmail.get().getPrimaryMobile();
            if (primaryMobile.equals(registeredMobile)) {
                matchedUser = new MatchedUser(patientByEmail.get().getId(), UserType.PATIENT, email);
            }
        } else if (doctorByEmail.isPresent()) {
            registeredMobile = doctorByEmail.get().getPrimaryMobile();
            if (primaryMobile.equals(registeredMobile)) {
                matchedUser = new MatchedUser(doctorByEmail.get().getId(), UserType.DOCTOR, email);
            }
        } else if (clinicByEmail.isPresent()) {
            registeredMobile = clinicByEmail.get().getPrimaryMobile();
            if (primaryMobile.equals(registeredMobile)) {
                matchedUser = new MatchedUser(clinicByEmail.get().getId(), UserType.CLINIC, email);
            }
        }

        if (matchedUser == null) {
            log.warn("[FORGOT PASSWORD FAILURE] Mobile number mismatch for email: '{}'. Provided mobile: '{}', Expected registered mobile ending in: '...{}'",
                    email,
                    primaryMobile,
                    (registeredMobile != null && registeredMobile.length() >= 4) ? registeredMobile.substring(registeredMobile.length() - 4) : "****");
            throw new BadRequestException("The primary mobile number does not match our records for this email address.");
        }

        // 3. User verified - invalidate older tokens
        passwordResetTokenRepository.invalidateActiveTokensForEmail(email);

        // 4. Generate 6-digit secure numeric OTP (100000 - 999999)
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

        // 5. Deliver OTP via configured Email and SMS services
        otpNotificationService.sendEmailOtp(email, otp);
        otpNotificationService.sendSmsOtp(primaryMobile, otp);

        log.info("==========================================================");
        log.info("[PASSWORD RESET OTP DISPATCHED] User: {} ({}) | Mobile: {}", email, matchedUser.userType, primaryMobile);
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
            log.warn("[PASSWORD RESET FAILURE] Expired OTP entered for email: '{}'", email);
            throw new BadRequestException("OTP has expired. Please request a new OTP.");
        }

        if (tokenEntity.getAttemptCount() >= MAX_OTP_ATTEMPTS) {
            tokenEntity.setUsed(true);
            passwordResetTokenRepository.save(tokenEntity);
            log.warn("[PASSWORD RESET FAILURE] Max OTP attempts exceeded for email: '{}'", email);
            throw new BadRequestException("Maximum verification attempts exceeded. Please request a new OTP.");
        }

        if (!tokenEntity.getOtp().equals(rawOtp)) {
            tokenEntity.setAttemptCount(tokenEntity.getAttemptCount() + 1);
            passwordResetTokenRepository.save(tokenEntity);
            int remaining = MAX_OTP_ATTEMPTS - tokenEntity.getAttemptCount();
            log.warn("[PASSWORD RESET FAILURE] Invalid OTP attempt for email: '{}'. Remaining: {}", email, remaining);
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

        log.info("[PASSWORD RESET SUCCESS] Successfully updated password for user: '{}' ({})", email, tokenEntity.getUserType());
    }
}
