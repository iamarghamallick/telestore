package com.argha.telestore.repository;

import com.argha.telestore.entity.Media;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends MongoRepository<Media, String> {

    List<Media> findByFilenameContainingIgnoreCase(String query);

}
