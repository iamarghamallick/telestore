package com.argha.telestore.repository;

import com.argha.telestore.entity.Media;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MediaRepository extends MongoRepository<Media, String> {

    List<Media> findAllByUserId(String userId);

    Optional<Media> findByUserIdAndId(String userId, String id);

    void deleteByUserIdAndId(String userId, String id);

}
