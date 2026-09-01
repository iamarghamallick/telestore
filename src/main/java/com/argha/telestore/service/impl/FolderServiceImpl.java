package com.argha.telestore.service.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.argha.telestore.dto.folder.CreateFolderRequest;
import com.argha.telestore.dto.folder.UpdateFolderRequest;
import com.argha.telestore.entity.Folder;
import com.argha.telestore.repository.FolderRepository;
import com.argha.telestore.service.FolderService;

@Service
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepo;

    public FolderServiceImpl(FolderRepository folderRepo) {
        this.folderRepo = folderRepo;
    }

    public Folder createFolder(String userId, CreateFolderRequest request) {
        String name = request.getName().trim();
        String parentFolderId = request.getParentFolderId();

        if (parentFolderId != null && !parentFolderId.isBlank()) {
            folderRepo.findByUserIdAndId(userId, parentFolderId)
                    .orElseThrow(() -> new RuntimeException("Parent folder not found"));
        } else {
            parentFolderId = null;
        }

        if (folderRepo.existsByUserIdAndParentFolderIdAndName(userId, parentFolderId, name)) {
            throw new RuntimeException("A folder with this name already exists");
        }

        Folder folder = new Folder();
        folder.setUserId(userId);
        folder.setName(name);
        folder.setParentFolderId(parentFolderId);
        folder.setCreatedAt(Instant.now());
        folder.setUpdatedAt(Instant.now());

        return folderRepo.save(folder);
    }

    public List<Folder> getAllFolders(String userId) {
        return folderRepo.findByUserId(userId);
    }

    public Folder getFolder(String userId, String id) {
        return folderRepo.findByUserIdAndId(userId, id).orElseThrow(() -> new RuntimeException("Folder not found"));
    }

    public List<Folder> getChildFolders(String userId, String parentFolderId) {
        return folderRepo.findByUserIdAndParentFolderId(userId, parentFolderId);
    }

    public Folder updateFolder(String userId, String id, UpdateFolderRequest request) {

        Folder folder = getFolder(userId, id);

        String newName = request.getName();

        if (!folder.getName().equals(newName)
                && folderRepo.existsByUserIdAndParentFolderIdAndName(userId, folder.getParentFolderId(), newName)) {
            throw new RuntimeException(
                    "A folder with this name already exists");
        }

        folder.setName(newName);
        folder.setUpdatedAt(Instant.now());

        return folderRepo.save(folder);
    }

    public void deleteFolder(String userId, String id) {
        folderRepo.deleteByUserIdAndId(userId, id);
    }

    public boolean existFolder(String userId, String folderId) {
        return folderId == null || folderRepo.existsByUserIdAndId(userId, folderId);
    }
}
