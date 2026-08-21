package com.argha.telestore.service;

import com.argha.telestore.dto.media.UpdateMediaRequest;
import com.argha.telestore.entity.Media;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MediaService {

    Media upload(String userId, MultipartFile file, String folderId) throws IOException;

    List<Media> getAllMedia(String userId);

    Media getById(String userId, String id);

    byte[] download(String userId, String mediaId);

    void deleteFile(String userId, String id);

    Media updateMedia(String userId, String id, UpdateMediaRequest request);

    Page<Media> searchMedia(
            String userId,
            String q,
            String type,
            String folderId,
            int page,
            int size,
            String sortBy,
            String sortDir);
}
