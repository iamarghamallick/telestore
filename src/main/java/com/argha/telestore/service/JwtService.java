package com.argha.telestore.service;

import java.util.Date;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;

public interface JwtService {

    String generateToken(String userId, String email);

    String generateRefreshToken();

    String extractEmail(String token);

    String extractUserId(String token);

    Date extractExpiration(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    Boolean validateToken(String token, UserDetails userDetails);
}
