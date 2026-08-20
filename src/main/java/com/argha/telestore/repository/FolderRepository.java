package com.argha.telestore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.argha.telestore.entity.Folder;

public interface FolderRepository extends MongoRepository<Folder, String> {

    Optional<Folder> findByUserIdAndId(String userId, String parentFoderId);

    boolean existsByUserIdAndParentFolderIdAndName(String userId, String parentFolderId, String name);

    List<Folder> findByUserId(String userId);

    List<Folder> findByUserIdAndParentFolderId(String userId, String parentFolderId);

    void deleteByUserIdAndId(String userId, String id);

    boolean existsByUserIdAndId(String userId, String folderId);

}
