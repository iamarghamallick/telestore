package com.argha.telestore.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.argha.telestore.entity.Folder;

@Repository
public interface FolderRepository extends MongoRepository<Folder, String> {

    boolean existsByParentFolderIdAndName(String parentFolderId, String name);

    List<Folder> findByParentFolderId(String parentFolderId);

}
