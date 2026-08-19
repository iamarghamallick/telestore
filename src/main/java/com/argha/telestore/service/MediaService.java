package com.argha.telestore.service;

import com.argha.telestore.dto.media.UpdateMediaRequest;
import com.argha.telestore.dto.telegram.TelegramFile;
import com.argha.telestore.entity.Media;
import com.argha.telestore.entity.MediaType;
import com.argha.telestore.entity.TelegramMedia;
import com.argha.telestore.repository.MediaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class MediaService {

    private final MediaRepository mediaRepo;
    private final TelegramService telegramService;
    private final FolderService folderService;

    public MediaService(MediaRepository mediaRepo, TelegramService telegramService, FolderService folderService) {
        this.mediaRepo = mediaRepo;
        this.telegramService = telegramService;
        this.folderService = folderService;
    }

    public Media upload(MultipartFile file, String folderId) throws IOException {

        TelegramMedia telegramMedia = telegramService.uploadDocument(file);

        if (!folderService.existFolder(folderId)) {
            throw new RuntimeException("Folder does not exist");
        }

        Media media = new Media();

        media.setFilename(file.getOriginalFilename());
        media.setFolderId(folderId);
        media.setMimeType(file.getContentType());
        media.setSize(file.getSize());
        media.setExtension(getExtension(file.getOriginalFilename()));
        media.setMediaType(determineMediaType(file.getContentType()));
        media.setMedia(telegramMedia);
        media.setCreatedAt(Instant.now());
        media.setUpdatedAt(Instant.now());

        mediaRepo.save(media);

        return media;
    }

    public List<Media> getAllMedia() {
        return mediaRepo.findAll();
    }

    public Media getById(String id) {
        return mediaRepo.findById(id).orElse(null);
    }

    public byte[] download(String mediaId) {

        Media media = mediaRepo
                .findById(mediaId)
                .orElseThrow(() -> new RuntimeException(
                        "Media not found"));

        TelegramMedia telegramMedia = media.getMedia();

        if (telegramMedia == null ||
                telegramMedia.getFileId() == null) {

            throw new RuntimeException(
                    "Telegram file information not found");
        }

        // Step 1: Get current file path from Telegram
        TelegramFile telegramFile = telegramService.getFile(
                telegramMedia.getFileId());

        if (telegramFile.getFilePath() == null) {
            throw new RuntimeException(
                    "Telegram file path not found");
        }

        // Step 2: Download actual file
        return telegramService.downloadFile(telegramFile.getFilePath());
    }

    public void deleteFile(String id) {
        mediaRepo.deleteById(id);
    }

    public Media updateMedia(String id, UpdateMediaRequest request) {
        Optional<Media> mediaOpt = mediaRepo.findById(id);

        if (mediaOpt.isEmpty()) {
            throw new RuntimeException("Media not found");
        }

        Media media = mediaOpt.get();

        if (request.getFilename() != null) {
            media.setFilename(request.getFilename().concat(".").concat(media.getExtension()));
        }

        String folderId = request.getFolderId();
        if (!folderService.existFolder(folderId)) {
            throw new RuntimeException("Folder not found");
        }

        media.setFolderId(folderId);
        media.setUpdatedAt(Instant.now());

        Media updatedMedia = mediaRepo.save(media);
        return updatedMedia;
    }

    private String getExtension(String filename) {

        if (filename == null || filename.isBlank()) {
            return "";
        }

        int index = filename.lastIndexOf(".");

        if (index == -1 || index == filename.length() - 1) {
            return "";
        }

        return filename
                .substring(index + 1)
                .toLowerCase();
    }

    private MediaType determineMediaType(String contentType) {

        if (contentType == null) {
            return MediaType.OTHER;
        }

        if (contentType.startsWith("image/")) {
            return MediaType.IMAGE;
        }

        if (contentType.startsWith("video/")) {
            return MediaType.VIDEO;
        }

        if (contentType.startsWith("audio/")) {
            return MediaType.AUDIO;
        }

        if (contentType.equals("application/pdf")
                || contentType.startsWith("text/")
                || contentType.contains("document")
                || contentType.contains("zip")) {

            return MediaType.DOCUMENT;
        }

        return MediaType.OTHER;
    }
}
