package com.argha.telestore.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.argha.telestore.entity.EmailVerificationToken;

public interface EmailVerificationTokenRepository extends MongoRepository<EmailVerificationToken, String> {

    Optional<EmailVerificationToken> findByTokenHashAndUsedFalse(String tokenHash);

    void deleteByUserId(String userId);
}