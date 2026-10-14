package com.backend.medicare.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String senderEmail;

    @Value("${medicare.email.sender-name:MediCare}")
    private String senderName;

    public EmailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String toEmail, String subject, String messageText) {
        if (toEmail == null || toEmail.trim().isEmpty()) {
            return;
        }

        if (mailSender == null) {
            logger.warn("JavaMailSender not configured. Skipping email to {}: {}", toEmail, subject);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (senderEmail != null && !senderEmail.isEmpty()) {
                message.setFrom(senderName + " <" + senderEmail + ">");
            }
            message.setTo(toEmail.trim());
            message.setSubject(subject);
            message.setText(messageText);

            mailSender.send(message);
            logger.info("Email sent successfully to {}", toEmail);
        } catch (Exception ex) {
            logger.error("Failed to send email to {}: {}", toEmail, ex.getMessage());
        }
    }
}
