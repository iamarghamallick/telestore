package com.argha.telestore.controller;

import com.argha.telestore.dto.ApiResponse;
import com.argha.telestore.dto.ApiResponseCode;
import com.argha.telestore.dto.media.UpdateMediaRequest;
import com.argha.telestore.entity.Media;
import com.argha.telestore.security.CustomUserDetails;
import com.argha.telestore.service.MediaService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Media> upload(@RequestParam("file") MultipartFile file,
            @RequestParam(value = "folderId", required = false) String folderId) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = userDetails.getUserId();
        Media media = mediaService.upload(userId, file, folderId);
        return ResponseEntity.ok(media);
    }

    // @GetMapping
    // public ResponseEntity<List<Media>> getAllMedia() {
    // List<Media> media = mediaService.getAllMedia();
    // return ResponseEntity.ok(media);
    // }

    @GetMapping
    public ResponseEntity<Page<Media>> getMedia(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String folderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = userDetails.getUserId();

        Page<Media> media = mediaService.searchMedia(
                userId,
                q,
                type,
                folderId,
                page,
                size,
                sortBy,
                sortDir);

        return ResponseEntity.ok(media);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Media> getMedia(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = userDetails.getUserId();

        Media media = mediaService.getById(userId, id);
        return ResponseEntity.ok(media);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable String id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = userDetails.getUserId();

        Media media = mediaService.getById(userId, id);

        byte[] file = mediaService.download(userId, id);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + media.getFilename() + "\"")
                .contentType(
                        MediaType.parseMediaType(
                                media.getMimeType()))
                .contentLength(file.length)
                .body(file);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Media> updateFile(@PathVariable String id, @Valid @RequestBody UpdateMediaRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = userDetails.getUserId();

        Media updatedMedia = mediaService.updateMedia(userId, id, request);
        return ResponseEntity.ok(updatedMedia);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteFile(@PathVariable String id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = userDetails.getUserId();

        mediaService.deleteFile(userId, id);

        ApiResponse response = new ApiResponse(204, ApiResponseCode.SUCCESS, "File deleted successfully");

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
