package com.argha.telestore.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.argha.telestore.service.EmailService;

@Service
@ConditionalOnProperty(name = "email.provider", havingValue = "brevo")
public class EmailServiceBrevo implements EmailService {

    private final RestClient restClient;
    private final String from;
    private final String frontendUrl;

    public EmailServiceBrevo(
            @Value("${brevo.api-key}") String apiKey,
            @Value("${brevo.from}") String from,
            @Value("${frontend.host-url}") String frontendUrl) {

        this.restClient = RestClient.builder()
                .baseUrl("https://api.brevo.com/v3")
                .defaultHeader("api-key", apiKey)
                .defaultHeader(
                        "accept",
                        MediaType.APPLICATION_JSON_VALUE)
                .build();

        this.from = from;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void sendPasswordResetEmail(
            String email,
            String token) {

        String resetUrl = frontendUrl
                + "/reset-password?token="
                + token;

        String html = """
                <!DOCTYPE html>
                <html>
                    <body>
                        <h2>Reset your TeleStore password</h2>

                        <p>
                            We received a request to reset your
                            TeleStore password.
                        </p>

                        <p>
                            Click the button below to create a new password:
                        </p>

                        <p>
                            <a href="%s"
                               style="
                                   display:inline-block;
                                   padding:12px 20px;
                                   background:#000;
                                   color:#fff;
                                   text-decoration:none;
                                   border-radius:6px;
                               ">
                                Reset Password
                            </a>
                        </p>

                        <p>
                            This link will expire in 30 minutes.
                        </p>

                        <p>
                            If you did not request a password reset,
                            you can safely ignore this email.
                        </p>

                        <p>
                            Regards,<br>
                            TeleStore Team
                        </p>
                    </body>
                </html>
                """.formatted(resetUrl);

        Map<String, Object> requestBody = Map.of(
                "sender", Map.of(
                        "email", from,
                        "name", "TeleStore"),
                "to", List.of(
                        Map.of(
                                "email", email)),
                "subject", "Reset your TeleStore password",
                "htmlContent", html);

        try {

            BrevoResponse response = restClient
                    .post()
                    .uri("/smtp/email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(BrevoResponse.class);

            System.out.println(
                    "Password reset email sent through Brevo. Message ID: "
                            + response.messageId());

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Failed to send password reset email through Brevo",
                    e);
        }
    }

    @Override
    public void sendVerificationEmail(String email, String token) {

        String verificationUrl = frontendUrl
                + "/verify-email?token="
                + token;

        String html = """
                <!DOCTYPE html>
                <html>
                    <body>
                        <h2>Verify your TeleStore email</h2>

                        <p>
                            Welcome to TeleStore!
                        </p>

                        <p>
                            Please verify your email address by clicking
                            the button below:
                        </p>

                        <p>
                            <a href="%s"
                               style="
                                   display:inline-block;
                                   padding:12px 20px;
                                   background:#000;
                                   color:#fff;
                                   text-decoration:none;
                                   border-radius:6px;
                               ">
                                Verify Email
                            </a>
                        </p>

                        <p>
                            This verification link will expire in 24 hours.
                        </p>

                        <p>
                            If you did not create a TeleStore account,
                            you can safely ignore this email.
                        </p>

                        <p>
                            Regards,<br>
                            TeleStore Team
                        </p>
                    </body>
                </html>
                """.formatted(verificationUrl);

        Map<String, Object> requestBody = Map.of(
                "sender", Map.of(
                        "email", from,
                        "name", "TeleStore"),
                "to", List.of(
                        Map.of(
                                "email", email)),
                "subject", "Verify your TeleStore email",
                "htmlContent", html);

        try {

            BrevoResponse response = restClient
                    .post()
                    .uri("/smtp/email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(BrevoResponse.class);

            System.out.println(
                    "Verification email sent through Brevo. Message ID: "
                            + response.messageId());

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Failed to send verification email through Brevo",
                    e);
        }
    }

    private record BrevoResponse(String messageId) {
    }
}
