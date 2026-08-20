package com.argha.telestore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.argha.telestore.dto.folder.CreateFolderRequest;
import com.argha.telestore.dto.folder.UpdateFolderRequest;
import com.argha.telestore.entity.Folder;
import com.argha.telestore.security.CustomUserDetails;
import com.argha.telestore.service.FolderService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/folder")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping
    public ResponseEntity<Folder> createFolder(@RequestBody CreateFolderRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = userDetails.getUserId();

        return ResponseEntity.ok(folderService.createFolder(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<Folder>> getAllFolders() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = userDetails.getUserId();

        return ResponseEntity.ok(folderService.getAllFolders(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Folder> getFolder(@PathVariable String id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = userDetails.getUserId();

        return ResponseEntity.ok(folderService.getFolder(userId, id));
    }

    @GetMapping("/children/{parentFolderId}")
    public ResponseEntity<List<Folder>> getChildFolders(@PathVariable String parentFolderId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = userDetails.getUserId();

        return ResponseEntity.ok(folderService.getChildFolders(userId, parentFolderId));
    }

    @PutMapping("{id}")
    public ResponseEntity<Folder> updateFolder(@PathVariable String id, @RequestBody UpdateFolderRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = userDetails.getUserId();

        return ResponseEntity.ok(folderService.updateFolder(userId, id, request));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable String id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = userDetails.getUserId();

        folderService.deleteFolder(userId, id);
        return ResponseEntity.noContent().build();
    }
}
