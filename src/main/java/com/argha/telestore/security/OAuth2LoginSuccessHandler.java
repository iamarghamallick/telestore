package com.argha.telestore.security;

import java.io.IOException;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.argha.telestore.entity.User;
import com.argha.telestore.repository.UserRepository;
import com.argha.telestore.service.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${oauth2.redirect-uri}")
    private String REDIRECT_URI;

    private final UserRepository userRepo;
    private final JwtService jwtService;

    public OAuth2LoginSuccessHandler(UserRepository userRepo, JwtService jwtService) {
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

        System.out.println("OAuth2 login successful!");
        System.out.println("Email: " + email);
        System.out.println("Name: " + name);
        System.out.println("JWT: " + token);

        response.sendRedirect(REDIRECT_URI + "?token=" + token);

        // response.setContentType("application/json");
        // response.getWriter().write(
        // "{\"token\":\"" + token + "\"}");
    }
}
