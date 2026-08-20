package com.argha.telestore.service;

import com.argha.telestore.dto.media.UpdateMediaRequest;
import com.argha.telestore.dto.telegram.TelegramFile;
import com.argha.telestore.entity.Media;
import com.argha.telestore.entity.MediaType;
import com.argha.telestore.entity.TelegramMedia;
import com.argha.telestore.repository.MediaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class MediaService {

    private final MediaRepository mediaRepo;
    private final MongoTemplate mongoTemplate;
    private final TelegramService telegramService;
    private final FolderService folderService;

    public MediaService(MediaRepository mediaRepo, MongoTemplate mongoTemplate, TelegramService telegramService,
            FolderService folderService) {
        this.mediaRepo = mediaRepo;
        this.mongoTemplate = mongoTemplate;
        this.telegramService = telegramService;
        this.folderService = folderService;
    }

    public Media upload(String userId, MultipartFile file, String folderId) throws IOException {

        TelegramMedia telegramMedia = telegramService.uploadDocument(file);

        Media media = new Media();

        media.setUserId(userId);
        media.setFilename(file.getOriginalFilename());
        media.setFolderId(folderService.existFolder(userId, folderId) ? folderId : null);
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

    public List<Media> getAllMedia(String userId) {
        return mediaRepo.findAllByUserId(userId);
    }

    public Media getById(String userId, String id) {
        return mediaRepo.findByUserIdAndId(userId, id).orElse(null);
    }

    public byte[] download(String userId, String mediaId) {

        Media media = mediaRepo
                .findByUserIdAndId(userId, mediaId)
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

    public void deleteFile(String userId, String id) {
        mediaRepo.deleteByUserIdAndId(userId, id);
    }

    public Media updateMedia(String userId, String id, UpdateMediaRequest request) {
        Media media = mediaRepo.findByUserIdAndId(userId, id)
                .orElseThrow(() -> new RuntimeException("Media not found"));

        if (request.getFilename() != null) {
            media.setFilename(request.getFilename().concat(".").concat(media.getExtension()));
        }

        String folderId = request.getFolderId();
        if (!folderService.existFolder(userId, folderId)) {
            throw new RuntimeException("Folder not found");
        }

        media.setFolderId(folderId);
        media.setUpdatedAt(Instant.now());

        Media updatedMedia = mediaRepo.save(media);
        return updatedMedia;
    }

    public Page<Media> searchMedia(
            String userId,
            String q,
            String type,
            String folderId,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        Query query = new Query();

        // IMPORTANT: Only get media belonging to this user
        query.addCriteria(
                Criteria.where("userId")
                        .is(userId));

        // Search by name
        if (q != null && !q.isBlank()) {
            query.addCriteria(
                    Criteria.where("filename")
                            .regex(Pattern.quote(q), "i"));
        }

        // Filter by type
        if (type != null && !type.isBlank()) {
            query.addCriteria(
                    Criteria.where("mediaType").is(type));
        }

        // Filter by folder
        if (folderId != null && !folderId.isBlank()) {
            query.addCriteria(
                    Criteria.where("folderId").is(folderId));
        }

        // Total count before pagination
        long total = mongoTemplate.count(query, Media.class);

        // Sorting
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        query.with(Sort.by(direction, sortBy));

        // Pagination
        Pageable pageable = PageRequest.of(page, size);

        query.with(pageable);

        List<Media> media = mongoTemplate.find(query, Media.class);

        return new PageImpl<>(
                media,
                pageable,
                total);
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
