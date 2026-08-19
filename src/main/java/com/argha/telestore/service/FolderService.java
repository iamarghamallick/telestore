package com.argha.telestore.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.argha.telestore.dto.folder.CreateFolderRequest;
import com.argha.telestore.dto.folder.UpdateFolderRequest;
import com.argha.telestore.entity.Folder;
import com.argha.telestore.repository.FolderRepository;

@Service
public class FolderService {

    private final FolderRepository folderRepo;

    public FolderService(FolderRepository folderRepo) {
        this.folderRepo = folderRepo;
    }

    public Folder createFolder(CreateFolderRequest request) {
        String name = request.getName().trim();
        String parentFoderId = request.getParentFolderId();

        if (parentFoderId != null && !parentFoderId.isBlank()) {
            folderRepo.findById(parentFoderId).orElseThrow(() -> new RuntimeException("Parent folder not found"));
        } else {
            parentFoderId = null;
        }

        if (folderRepo.existsByParentFolderIdAndName(request.getParentFolderId(), name)) {
            throw new RuntimeException("A folder with this name already exists");
        }

        Folder folder = new Folder();
        folder.setName(name);
        folder.setParentFolderId(parentFoderId);
        folder.setCreatedAt(Instant.now());
        folder.setUpdatedAt(Instant.now());

        return folderRepo.save(folder);
    }

    public List<Folder> getAllFolders() {
        return folderRepo.findAll();
    }

    public Folder getFolder(String id) {
        return folderRepo.findById(id).orElseThrow(() -> new RuntimeException("Folder not found"));
    }

    public List<Folder> getChildFolders(String parentFolderId) {
        return folderRepo.findByParentFolderId(parentFolderId);
    }

    public Folder updateFolder(String id, UpdateFolderRequest request) {

        Folder folder = getFolder(id);

        String newName = request.getName();

        if (!folder.getName().equals(newName)
                && folderRepo.existsByParentFolderIdAndName(folder.getParentFolderId(), newName)) {
            throw new RuntimeException(
                    "A folder with this name already exists");
        }

        folder.setName(newName);
        folder.setUpdatedAt(Instant.now());

        return folderRepo.save(folder);
    }

    public void deleteFolder(String id) {
        folderRepo.deleteById(id);
    }

    public boolean existFolder(String folderId) {
        return folderId == null || folderRepo.existsById(folderId);
    }
}
