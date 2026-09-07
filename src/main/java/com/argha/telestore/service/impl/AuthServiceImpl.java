package com.argha.telestore.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.argha.telestore.dto.auth.AuthResponse;
import com.argha.telestore.dto.auth.LoginRequest;
import com.argha.telestore.dto.auth.RegisterRequest;
import com.argha.telestore.entity.RefreshToken;
import com.argha.telestore.entity.User;
import com.argha.telestore.exception.UserNotFoundException;
import com.argha.telestore.exception.EmailNotVerifiedException;
import com.argha.telestore.exception.InvalidAccessTokenException;
import com.argha.telestore.exception.InvalidCredentialsException;
import com.argha.telestore.exception.UserAlreadyExistsException;
import com.argha.telestore.repository.RefreshTokenRepository;
import com.argha.telestore.repository.UserRepository;
import com.argha.telestore.service.AuthService;
import com.argha.telestore.service.EmailVerificationService;

@Service
public class AuthServiceImpl implements AuthService {

    private final RefreshTokenRepository refreshTokenRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtServiceImpl jwtService;
    private final EmailVerificationService emailVerificationService;

    public AuthServiceImpl(RefreshTokenRepository refreshTokenRepo, UserRepository userRepo,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtServiceImpl jwtService,
            EmailVerificationService emailVerificationService) {
        this.refreshTokenRepo = refreshTokenRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailVerificationService = emailVerificationService;
    }

    public User register(RegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setEmailVerified(false);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        User savedUser = userRepo.save(user);

        emailVerificationService.sendVerificationEmail(savedUser.getId());

        return savedUser;
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException(
                    "Please verify your email before logging in");
        }

        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            request.getPassword()));
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Invalid credentials!");
        }

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidCredentialsException("Invalid credentials!");
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail());

        String refreshTokenStr = jwtService.generateRefreshToken();

        refreshTokenRepo.deleteByUserId(user.getId());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setUserId(user.getId());
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshToken.setRevoked(false);

        RefreshToken savedRefreshToken = refreshTokenRepo.save(refreshToken);

        return new AuthResponse(token, savedRefreshToken.getToken());
    }

    public AuthResponse refresh(String oldRefreshToken) {

        RefreshToken savedRefreshToken = refreshTokenRepo.findByToken(oldRefreshToken).orElseThrow(() -> {
            return new InvalidAccessTokenException("Invalid refresh token. Please login again.");
        });

        if (savedRefreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked. Please login again.");
        }

        if (savedRefreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepo.delete(savedRefreshToken);
            throw new RuntimeException("Refresh token expired. Please login again.");
        }

        User user = userRepo.findById(savedRefreshToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail());
        String refreshTokenStr = jwtService.generateRefreshToken();

        refreshTokenRepo.delete(savedRefreshToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setUserId(user.getId());
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshToken.setRevoked(false);

        RefreshToken newRefreshToken = refreshTokenRepo.save(refreshToken);

        return new AuthResponse(token, newRefreshToken.getToken());
    }

    public void logout(String refreshToken) {
        RefreshToken savedRefreshToken = refreshTokenRepo.findByToken(refreshToken).orElseThrow(() -> {
            return new InvalidAccessTokenException("Invalid refresh token. Please login again.");
        });

        savedRefreshToken.setRevoked(true);

        refreshTokenRepo.save(savedRefreshToken);
    }
}
