package com.argha.telestore.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.argha.telestore.entity.PasswordResetToken;
import com.argha.telestore.entity.User;
import com.argha.telestore.exception.InvalidPasswordResetTokenException;
import com.argha.telestore.exception.UserNotFoundException;
import com.argha.telestore.repository.PasswordResetTokenRepository;
import com.argha.telestore.repository.RefreshTokenRepository;
import com.argha.telestore.repository.UserRepository;
import com.argha.telestore.service.EmailService;
import com.argha.telestore.service.PasswordResetService;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final SecureRandom secureRandom = new SecureRandom();
    private final UserRepository userRepo;
    private final PasswordResetTokenRepository passwordResetTokenRepo;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepo;

    public PasswordResetServiceImpl(UserRepository userRepo, PasswordResetTokenRepository passwordResetTokenRepo,
            EmailService emailService, PasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenRepo) {
        this.userRepo = userRepo;
        this.passwordResetTokenRepo = passwordResetTokenRepo;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepo = refreshTokenRepo;

    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("No such user found!"));

        passwordResetTokenRepo.deleteByUserId(user.getId());

        String rawToken = generateToken();

        String tokenHash = hashToken(rawToken);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUserId(user.getId());
        resetToken.setTokenHash(tokenHash);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        resetToken.setUsed(false);

        passwordResetTokenRepo.save(resetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), rawToken);
    }

    @Override
    public void resetPassword(String rawToken, String newPassword) throws InvalidPasswordResetTokenException {
        String tokenHash = hashToken(rawToken);

        PasswordResetToken resetToken = passwordResetTokenRepo.findByTokenHashAndUsedFalse(tokenHash)
                .orElseThrow(() -> new InvalidPasswordResetTokenException("Invalid or expired password reset token."));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidPasswordResetTokenException("Invalid or expired password reset token.");
        }

        User user = userRepo.findById(resetToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepo.save(user);

        resetToken.setUsed(true);

        passwordResetTokenRepo.save(resetToken);

        refreshTokenRepo.deleteByUserId(user.getId());
    }

    private String generateToken() {
        byte[] bytes = new byte[32];

        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
