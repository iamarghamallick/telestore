package com.argha.telestore.controller;

import com.argha.telestore.dto.media.UpdateMediaRequest;
import com.argha.telestore.entity.Media;
import com.argha.telestore.service.MediaService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
            @RequestParam("folderId") String folderId) throws IOException {
        Media media = mediaService.upload(file, folderId);
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

        Page<Media> media = mediaService.searchMedia(
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
        Media media = mediaService.getById(id);
        return ResponseEntity.ok(media);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable String id) {

        Media media = mediaService.getById(id);

        byte[] file = mediaService.download(id);

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
    public ResponseEntity<Media> updateFile(@PathVariable String id, @RequestBody UpdateMediaRequest request) {
        Media updatedMedia = mediaService.updateMedia(id, request);
        return ResponseEntity.ok(updatedMedia);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable String id) {
        mediaService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
}
