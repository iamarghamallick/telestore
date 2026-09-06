package com.argha.telestore.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetToken {

    @Id
    private String id;

    private String tokenHash;
    private String userId;
    private LocalDateTime expiresAt;
    private boolean used;
}
