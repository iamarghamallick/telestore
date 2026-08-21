package com.argha.telestore.service;

import com.argha.telestore.dto.telegram.TelegramFile;
import com.argha.telestore.entity.TelegramMedia;

import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;

import java.io.IOException;

public interface TelegramService {

    JsonNode getBotInfo();

    TelegramMedia uploadDocument(MultipartFile file) throws IOException;

    TelegramFile getFile(String fileId);

    byte[] downloadFile(String filePath);
}
