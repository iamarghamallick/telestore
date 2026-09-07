package com.argha.telestore.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.argha.telestore.service.EmailService;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

@Service
@ConditionalOnProperty(name = "email.provider", havingValue = "resend")
public class EmailServiceResend implements EmailService {

    private final Resend resend;
    private final String from;
    private final String frontendUrl;

    public EmailServiceResend(@Value("${resend.api-key}") String apiKey, @Value("${resend.from}") String from,
            @Value("${frontend.host-url}") String frontendUrl) {
        this.resend = new Resend(apiKey);
        this.from = from;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void sendPasswordResetEmail(String email, String token) {
        String resetUrl = frontendUrl
                + "/reset-password?token="
                + token;

        String html = """
                <html>
                    <body>
                        <h2>Reset your TeleStore password</h2>

                        <p>
                            We received a request to reset your TeleStore password.
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

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(from)
                .to(email)
                .subject("Reset your TeleStore password")
                .html(html)
                .build();

        try {
            CreateEmailResponse response = resend
                    .emails()
                    .send(params);

            System.out.println(
                    "Password reset email sent. Resend ID: "
                            + response.getId());

        } catch (ResendException e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Failed to send password reset email",
                    e);
        }
    }

    @Override
    public void sendVerificationEmail(String email, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendVerificationEmail'");
    }

}
