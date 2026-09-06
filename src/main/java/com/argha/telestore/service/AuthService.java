package com.argha.telestore.service;

import com.argha.telestore.dto.auth.AuthResponse;
import com.argha.telestore.dto.auth.LoginRequest;
import com.argha.telestore.dto.auth.RegisterRequest;
import com.argha.telestore.entity.User;

public interface AuthService {

    User register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(String oldRefreshToken);

    void logout(String refreshToken);
}
