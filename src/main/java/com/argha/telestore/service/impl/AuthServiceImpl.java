package com.argha.telestore.service.impl;

import java.time.Instant;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.argha.telestore.dto.auth.LoginRequest;
import com.argha.telestore.dto.auth.LoginResponse;
import com.argha.telestore.dto.auth.ResgisterRequest;
import com.argha.telestore.entity.User;
import com.argha.telestore.exception.UserNotFoundException;
import com.argha.telestore.exception.InvalidCredentialsException;
import com.argha.telestore.exception.UserAlreadyExistsException;
import com.argha.telestore.repository.UserRepository;
import com.argha.telestore.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtServiceImpl jwtService;

    public AuthServiceImpl(UserRepository userRepo, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtServiceImpl jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public User register(ResgisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        return userRepo.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword()));

        if (authentication.isAuthenticated()) {

            String token = jwtService.generateToken(
                    user.getId(),
                    user.getEmail());

            return new LoginResponse(token);
        }

        throw new InvalidCredentialsException("Invalid credentials!");
    }
}
