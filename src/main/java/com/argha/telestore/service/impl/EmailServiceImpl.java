package com.argha.telestore.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.argha.telestore.service.EmailService;

@Service
@ConditionalOnProperty(name = "email.provider", havingValue = "gmail")
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final String frontendUrl;
    private final String from;

    public EmailServiceImpl(
            JavaMailSender mailSender,
            @Value("${frontend.host-url}") String frontendUrl,
            @Value("${spring.mail.username}") String from) {

        this.mailSender = mailSender;
        this.frontendUrl = frontendUrl;
        this.from = from;
    }

    @Override
    public void sendPasswordResetEmail(String email, String token) {
        String resetUrl = frontendUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(email);
        message.setSubject("Reset your TeleStore password");

        message.setText("""
                Hello,

                We received a request to reset your TeleStore password.

                Click the link below to create a new password:

                %s

                This link will expire in 30 minutes.

                If you did not request a password reset, you can safely ignore this email.

                Regards,
                TeleStore Team
                """.formatted(resetUrl));

        mailSender.send(message);
    }

}
