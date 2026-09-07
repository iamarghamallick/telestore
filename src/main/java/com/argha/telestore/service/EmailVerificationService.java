package com.argha.telestore.service;

import com.argha.telestore.exception.InvalidEmailVerificationTokenException;

public interface EmailVerificationService {

    void sendVerificationEmail(String userId) throws InvalidEmailVerificationTokenException;

    void verifyEmail(String token) throws InvalidEmailVerificationTokenException;

    void resendVerificationEmail(String email);
}