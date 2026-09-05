package com.argha.telestore.security;

import java.io.IOException;
import java.net.HttpCookie;
import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.argha.telestore.entity.RefreshToken;
import com.argha.telestore.entity.User;
import com.argha.telestore.repository.RefreshTokenRepository;
import com.argha.telestore.repository.UserRepository;
import com.argha.telestore.service.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${oauth2.redirect-uri}")
    private String REDIRECT_URI;

    @Value("${app.security.cookie.secure}")
    private boolean isCookieSecure;

    @Value("${app.security.cookie.same-site}")
    private String cookieSameSite;

    private final RefreshTokenRepository refreshTokenRepo;
    private final UserRepository userRepo;
    private final JwtService jwtService;

    public OAuth2LoginSuccessHandler(RefreshTokenRepository refreshTokenRepo, UserRepository userRepo,
            JwtService jwtService) {
        this.refreshTokenRepo = refreshTokenRepo;
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepo.findByEmail(email).orElseGet(() -> {

            User newUser = new User();

            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(null);
            newUser.setCreatedAt(Instant.now());
            newUser.setUpdatedAt(Instant.now());

            return userRepo.save(newUser);
        });

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        String refreshTokenStr = jwtService.generateRefreshToken();

        refreshTokenRepo.deleteByUserId(user.getId());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setUserId(user.getId());
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshToken.setRevoked(false);

        RefreshToken savedRefreshToken = refreshTokenRepo.save(refreshToken);

        ResponseCookie cookie = createRefreshTokenCookie(savedRefreshToken.getToken());

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        response.sendRedirect(REDIRECT_URI);
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
