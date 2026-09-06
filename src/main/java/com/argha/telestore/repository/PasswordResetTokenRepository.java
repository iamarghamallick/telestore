package com.argha.telestore.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.argha.telestore.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {

    Optional<PasswordResetToken> findByTokenHashAndUsedFalse(String tokenHash);

    void deleteByUserId(String userId);
}