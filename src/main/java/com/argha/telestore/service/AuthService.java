package com.argha.telestore.service;

import com.argha.telestore.dto.auth.LoginRequest;
import com.argha.telestore.dto.auth.LoginResponse;
import com.argha.telestore.dto.auth.RefreshTokenRequest;
import com.argha.telestore.dto.auth.ResgisterRequest;
import com.argha.telestore.entity.User;

public interface AuthService {

    User register(ResgisterRequest request);

    LoginResponse login(LoginRequest request);

    LoginResponse refresh(RefreshTokenRequest request);
}
