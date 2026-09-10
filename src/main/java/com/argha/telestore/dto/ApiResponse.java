package com.argha.telestore.dto;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Getter;

@Getter
public class ApiResponse {

    private final int status;
    private final ApiResponseCode code;
    private final String message;
    private final LocalDateTime timestamp;
    private final Map<String, String> errors;

    public ApiResponse(
            int status,
            ApiResponseCode code,
            String message) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.errors = null;
    }

    public ApiResponse(
            int status,
            ApiResponseCode code,
            String message,
            Map<String, String> errors) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.errors = errors;
    }
}
