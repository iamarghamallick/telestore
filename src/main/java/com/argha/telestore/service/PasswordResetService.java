package com.argha.telestore.service;

import com.argha.telestore.exception.InvalidPasswordResetTokenException;

public interface PasswordResetService {

    void forgotPassword(String email);

    void resetPassword(String rawToken, String newPassword) throws InvalidPasswordResetTokenException;
}
