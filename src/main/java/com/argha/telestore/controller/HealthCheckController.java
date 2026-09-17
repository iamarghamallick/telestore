package com.argha.telestore.controller;

import org.springframework.web.bind.annotation.RestController;

import com.argha.telestore.dto.ApiResponse;
import com.argha.telestore.dto.ApiResponseCode;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/health-check")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<ApiResponse> healthCheck() {

        ApiResponse response = new ApiResponse(HttpStatus.OK.value(), ApiResponseCode.SUCCESS,
                "Telestore server is healthy!");
        return ResponseEntity.ok(response);
    }

}
