package com.argha.telestore.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.argha.telestore.entity.EmailVerificationToken;
import com.argha.telestore.entity.User;
import com.argha.telestore.exception.InvalidEmailVerificationTokenException;
import com.argha.telestore.repository.EmailVerificationTokenRepository;
import com.argha.telestore.repository.UserRepository;
import com.argha.telestore.service.EmailService;
import com.argha.telestore.service.EmailVerificationService;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private final SecureRandom secureRandom = new SecureRandom();

    public EmailVerificationServiceImpl(
            EmailVerificationTokenRepository tokenRepository,
            UserRepository userRepository,
            EmailService emailService) {

        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    public void sendVerificationEmail(String userId) throws InvalidEmailVerificationTokenException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isEmailVerified()) {
            throw new InvalidEmailVerificationTokenException("Invalid verification request!");
        }

        // Invalidate previous verification tokens
        tokenRepository.deleteByUserId(userId);

        String rawToken = generateToken();
        String tokenHash = hashToken(rawToken);

        EmailVerificationToken verificationToken = new EmailVerificationToken();

        verificationToken.setTokenHash(tokenHash);
        verificationToken.setUserId(userId);
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        verificationToken.setUsed(false);

        tokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(user.getEmail(), rawToken);
    }

    @Override
    public void verifyEmail(String token) throws InvalidEmailVerificationTokenException {

        String tokenHash = hashToken(token);

        EmailVerificationToken verificationToken = tokenRepository
                .findByTokenHashAndUsedFalse(tokenHash)
                .orElseThrow(() -> new InvalidEmailVerificationTokenException(
                        "Invalid or expired verification token"));

        if (verificationToken
                .getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new InvalidEmailVerificationTokenException(
                    "Invalid or expired verification token");
        }

        User user = userRepository
                .findById(verificationToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmailVerified(true);

        userRepository.save(user);

        verificationToken.setUsed(true);

        tokenRepository.save(verificationToken);
    }

    @Override
    public void resendVerificationEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElse(null);

        // Don't reveal whether the account exists
        if (user == null) {
            return;
        }

        // Already verified → nothing to send
        if (user.isEmailVerified()) {
            return;
        }

        // Invalidate any previous verification tokens
        tokenRepository.deleteByUserId(user.getId());

        String rawToken = generateToken();
        String tokenHash = hashToken(rawToken);

        EmailVerificationToken verificationToken = new EmailVerificationToken();

        verificationToken.setTokenHash(tokenHash);
        verificationToken.setUserId(user.getId());
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        verificationToken.setUsed(false);

        tokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(
                user.getEmail(),
                rawToken);
    }

    private String generateToken() {

        byte[] bytes = new byte[32];

        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hashToken(String token) {

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8));

            return java.util.HexFormat
                    .of()
                    .formatHex(hash);

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 algorithm not available",
                    e);
        }
    }
}