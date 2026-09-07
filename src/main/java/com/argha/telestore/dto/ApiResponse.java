package com.argha.telestore.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse {

    private int status;
    private String message;
    private LocalDateTime timestamp;
}
