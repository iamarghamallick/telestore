package com.argha.telestore.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.argha.telestore.entity.RefreshToken;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    void deleteByUserId(String userId);

    Optional<RefreshToken> findByToken(String token);

}
