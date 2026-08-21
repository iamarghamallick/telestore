package com.argha.telestore.service;

import java.util.List;

import com.argha.telestore.dto.folder.CreateFolderRequest;
import com.argha.telestore.dto.folder.UpdateFolderRequest;
import com.argha.telestore.entity.Folder;

public interface FolderService {

    Folder createFolder(String userId, CreateFolderRequest request);

    List<Folder> getAllFolders(String userId);

    Folder getFolder(String userId, String id);

    List<Folder> getChildFolders(String userId, String parentFolderId);

    Folder updateFolder(String userId, String id, UpdateFolderRequest request);

    void deleteFolder(String userId, String id);

    boolean existFolder(String userId, String folderId);
}
