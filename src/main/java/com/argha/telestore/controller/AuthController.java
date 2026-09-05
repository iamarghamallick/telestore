package com.argha.telestore.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.argha.telestore.dto.auth.AuthResponse;
import com.argha.telestore.dto.auth.LoginRequest;
import com.argha.telestore.dto.auth.LoginResponse;
import com.argha.telestore.dto.auth.ResgisterRequest;
import com.argha.telestore.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${app.security.cookie.secure}")
    private boolean isCookieSecure;

    @Value("${app.security.cookie.same-site}")
    private String cookieSameSite;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody ResgisterRequest request) {
        authService.register(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);

        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(authResponse.getRefreshToken());

        LoginResponse loginResponse = new LoginResponse(authResponse.getToken());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString()).body(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @CookieValue(name = "refreshToken", required = false) String oldRefreshToken) {
        AuthResponse authResponse = authService.refresh(oldRefreshToken);

        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(authResponse.getRefreshToken());

        LoginResponse loginResponse = new LoginResponse(authResponse.getToken());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString()).body(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        authService.logout(refreshToken);

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(isCookieSecure)
                .path("/api/auth")
                .maxAge(0)
                .sameSite(cookieSameSite)
                .build();

        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, deleteCookie.toString()).build();
    }

    private ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(isCookieSecure)
                .path("/api/auth")
                .maxAge(7 * 24 * 60 * 60) // 7 days
                .sameSite(cookieSameSite)
                .build();
    }
}
