package com.argha.telestore.service;

public interface EmailService {

    void sendPasswordResetEmail(String email, String token);

    void sendVerificationEmail(String email, String token);
}