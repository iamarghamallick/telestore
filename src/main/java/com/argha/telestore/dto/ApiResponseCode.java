package com.argha.telestore.dto;

public enum ApiResponseCode {

    // =========================
    // General
    // =========================

    SUCCESS,
    OPERATION_SUCCESSFUL,
    INVALID_REQUEST,
    INTERNAL_SERVER_ERROR,
    RESOURCE_NOT_FOUND,
    METHOD_NOT_ALLOWED,
    UNSUPPORTED_MEDIA_TYPE,
    DATA_CONFLICT,
    FILE_TOO_LARGE,

    // =========================
    // Authentication
    // =========================

    LOGIN_SUCCESSFUL,
    LOGIN_FAILED,
    INVALID_CREDENTIALS,
    UNAUTHORIZED,
    ACCESS_DENIED,
    TOKEN_MISSING,
    TOKEN_INVALID,
    TOKEN_EXPIRED,
    REFRESH_TOKEN_INVALID,
    REFRESH_TOKEN_EXPIRED,
    REFRESH_TOKEN_REVOKED,
    TOKEN_REFRESH_SUCCESSFUL,

    // =========================
    // Registration
    // =========================

    REGISTRATION_SUCCESSFUL,
    EMAIL_ALREADY_EXISTS,
    USER_ALREADY_EXISTS,
    USER_NOT_FOUND,

    // =========================
    // Email Verification
    // =========================

    EMAIL_NOT_VERIFIED,
    VERIFICATION_EMAIL_SENT,
    EMAIL_VERIFIED_SUCCESSFULLY,
    INVALID_EMAIL_VERIFICATION_TOKEN,
    EMAIL_VERIFICATION_TOKEN_EXPIRED,
    EMAIL_ALREADY_VERIFIED,
    VERIFICATION_EMAIL_RATE_LIMITED,

    // =========================
    // Password
    // =========================

    PASSWORD_CHANGED_SUCCESSFULLY,
    CURRENT_PASSWORD_INCORRECT,
    PASSWORD_RESET_EMAIL_SENT,
    INVALID_PASSWORD_RESET_TOKEN,
    PASSWORD_RESET_TOKEN_EXPIRED,
    PASSWORD_RESET_SUCCESSFUL,

    // =========================
    // OAuth
    // =========================

    OAUTH_LOGIN_SUCCESSFUL,
    OAUTH_LOGIN_FAILED,
    OAUTH_USER_NOT_FOUND,

    // =========================
    // Validation
    // =========================

    VALIDATION_FAILED,
    REQUIRED_FIELD_MISSING,
    INVALID_EMAIL,
    INVALID_PASSWORD,

    // =========================
    // Rate Limiting
    // =========================

    TOO_MANY_REQUESTS,
    RATE_LIMIT_EXCEEDED,

    FOLDER_NOT_FOUND
}