package com.argha.telestore.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.argha.telestore.exception.InvalidAccessTokenException;
import com.argha.telestore.service.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public JwtAuthFilter(UserDetailsService userDetailsService, JwtService jwtService,
            AuthenticationEntryPoint authenticationEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // No Authorization header.
        // Let Spring Security decide whether the endpoint requires authentication.
        if (authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization header exists but is not Bearer.
        if (!authHeader.startsWith("Bearer ")) {

            authenticationEntryPoint.commence(
                    request,
                    response,
                    new InvalidAccessTokenException(
                            "Invalid authorization header"));

            return;
        }

        String token = authHeader.substring(7);

        // Empty Bearer token.
        if (token.isBlank()) {

            authenticationEntryPoint.commence(
                    request,
                    response,
                    new InvalidAccessTokenException(
                            "Access token is required"));

            return;
        }

        try {

            String username = jwtService.extractEmail(token);

            if (username == null) {

                authenticationEntryPoint.commence(
                        request,
                        response,
                        new InvalidAccessTokenException(
                                "Invalid access token"));

                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (!jwtService.validateToken(token, userDetails)) {

                    authenticationEntryPoint.commence(
                            request,
                            response,
                            new InvalidAccessTokenException(
                                    "Invalid access token"));

                    return;
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {

            SecurityContextHolder.clearContext();

            authenticationEntryPoint.commence(
                    request,
                    response,
                    new InvalidAccessTokenException(
                            "Access token has expired"));

        } catch (SignatureException e) {

            SecurityContextHolder.clearContext();

            authenticationEntryPoint.commence(
                    request,
                    response,
                    new InvalidAccessTokenException(
                            "Invalid access token"));

        } catch (MalformedJwtException e) {

            SecurityContextHolder.clearContext();

            authenticationEntryPoint.commence(
                    request,
                    response,
                    new InvalidAccessTokenException(
                            "Invalid access token"));

        } catch (UnsupportedJwtException e) {

            SecurityContextHolder.clearContext();

            authenticationEntryPoint.commence(
                    request,
                    response,
                    new InvalidAccessTokenException(
                            "Invalid access token"));

        } catch (IllegalArgumentException e) {

            SecurityContextHolder.clearContext();

            authenticationEntryPoint.commence(
                    request,
                    response,
                    new InvalidAccessTokenException(
                            "Invalid access token"));

        } catch (UsernameNotFoundException e) {

            SecurityContextHolder.clearContext();

            authenticationEntryPoint.commence(
                    request,
                    response,
                    new InvalidAccessTokenException(
                            "User associated with token no longer exists"));
        }
    }

}