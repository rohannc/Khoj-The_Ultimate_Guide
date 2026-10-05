package com.rohan.Khoj.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class OtpNotificationService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${application.notification.mail.from:${spring.mail.username:support@khojhealth.com}}")
    private String mailFrom;

    @Value("${application.notification.sms.from-sender-id:KHOJ}")
    private String smsSenderId;

    @Value("${application.notification.sms.api-url:}")
    private String smsApiUrl;

    @Value("${application.notification.sms.api-key:}")
    private String smsApiKey;

    /**
     * Sends the OTP to the user's email address.
     * If JavaMailSender is configured (e.g. spring.mail.host), it sends an actual email.
     * Otherwise, it logs the simulated email dispatch clearly.
     *
     * @param toEmail The recipient's email address.
     * @param otp The 6-digit OTP.
     */
    public void sendEmailOtp(String toEmail, String otp) {
        log.info("[EMAIL NOTIFICATION] Preparing OTP email FROM: '{}' TO: '{}'", mailFrom, toEmail);

        if (mailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(mailFrom);
                message.setTo(toEmail);
                message.setSubject("Khoj Healthcare - Password Reset OTP");
                message.setText("Dear User,\n\n" +
                        "You requested to reset your password for your Khoj Healthcare account.\n\n" +
                        "Your One-Time Password (OTP) is: " + otp + "\n\n" +
                        "This OTP is valid for 10 minutes. For security, please do not share this code with anyone.\n\n" +
                        "If you did not request this, please ignore this email or contact support.\n\n" +
                        "Warm regards,\n" +
                        "Khoj Healthcare Team");

                mailSender.send(message);
                log.info("[EMAIL NOTIFICATION] Successfully sent real email to {}", toEmail);
                return;
            } catch (Exception e) {
                log.error("[EMAIL NOTIFICATION] Failed to send email via SMTP: {}. Falling back to log display.", e.getMessage());
            }
        } else {
            log.info("[EMAIL NOTIFICATION] (SMTP not configured) Simulated Email Dispatch:");
            log.info("  >> FROM: {}", mailFrom);
            log.info("  >> TO: {}", toEmail);
            log.info("  >> SUBJECT: Khoj Healthcare - Password Reset OTP");
            log.info("  >> CONTENT: Your OTP is {}", otp);
        }
    }

    /**
     * Sends the OTP to the user's mobile phone via SMS / WhatsApp.
     * If an SMS API URL and Key are provided, it dispatches the SMS via HTTP.
     * Otherwise, it logs the simulated SMS dispatch clearly.
     *
     * @param toMobile The recipient's 10-digit primary mobile number.
     * @param otp The 6-digit OTP.
     */
    public void sendSmsOtp(String toMobile, String otp) {
        log.info("[SMS NOTIFICATION] Preparing OTP SMS FROM SENDER: '{}' TO: '+91-{}'", smsSenderId, toMobile);

        if (smsApiUrl != null && !smsApiUrl.isBlank() && smsApiKey != null && !smsApiKey.isBlank()) {
            try {
                // Generic configurable template URL or query replacement (e.g. Fast2SMS / Twilio / MSG91)
                String requestUrl = smsApiUrl
                        .replace("{API_KEY}", smsApiKey)
                        .replace("{MOBILE}", toMobile)
                        .replace("{OTP}", otp)
                        .replace("{SENDER_ID}", smsSenderId);

                restTemplate.getForObject(requestUrl, String.class);
                log.info("[SMS NOTIFICATION] Successfully dispatched SMS to +91-{}", toMobile);
                return;
            } catch (Exception e) {
                log.error("[SMS NOTIFICATION] Failed to dispatch SMS via gateway: {}. Falling back to log display.", e.getMessage());
            }
        } else {
            log.info("[SMS NOTIFICATION] (SMS Gateway not configured) Simulated SMS Dispatch:");
            log.info("  >> FROM SENDER ID: {}", smsSenderId);
            log.info("  >> TO NUMBER: +91-{}", toMobile);
            log.info("  >> MESSAGE: Khoj Healthcare: Your OTP for password reset is {}. Valid for 10 minutes.", otp);
        }
    }
}
