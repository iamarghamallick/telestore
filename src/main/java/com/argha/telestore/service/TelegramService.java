package com.argha.telestore.service;

import com.argha.telestore.dto.TelegramDocument;
import com.argha.telestore.dto.TelegramFile;
import com.argha.telestore.dto.TelegramMessage;
import com.argha.telestore.dto.TelegramResponse;
import com.argha.telestore.entity.TelegramMedia;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;

import java.io.IOException;

@Service
public class TelegramService {

    private final RestClient telegramClient;
    private final RestClient telegramFileClient;
    private final String chatId;

    public TelegramService(@Qualifier("telegramRestClient") RestClient telegramClient,
            @Qualifier("telegramFileClient") RestClient telegramFileClient,
            @Value("${telegram.chat-id}") String chatId) {

        this.telegramClient = telegramClient;
        this.telegramFileClient = telegramFileClient;
        this.chatId = chatId;
    }

    public JsonNode getBotInfo() {
        return telegramClient.get().uri("/getMe").retrieve().body(JsonNode.class);
    }

    public TelegramMedia uploadDocument(MultipartFile file)
            throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String filename = file.getOriginalFilename();

        if (filename == null || filename.isBlank()) {
            filename = "file";
        }

        byte[] fileBytes = file.getBytes();

        String finalFilename = filename;
        ByteArrayResource resource = new ByteArrayResource(fileBytes) {

            @Override
            public String getFilename() {
                return finalFilename;
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("chat_id", chatId);
        body.add("document", resource);

        TelegramResponse<TelegramMessage> response = telegramClient
                .post()
                .uri("/sendDocument")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(
                        new ParameterizedTypeReference<TelegramResponse<TelegramMessage>>() {
                        });

        if (response == null || !response.isOk()) {
            throw new RuntimeException(
                    "Telegram upload failed: " +
                            (response != null
                                    ? response.getDescription()
                                    : "Empty response"));
        }

        TelegramMessage message = response.getResult();

        if (message == null) {
            throw new RuntimeException(
                    "Telegram returned no message");
        }

        TelegramDocument document = message.getDocument();

        if (document == null) {
            throw new RuntimeException(
                    "Telegram returned no document");
        }

        System.out.println("Message ID: " + message.getMessageId());
        System.out.println("File ID: " + document.getFileId());
        System.out.println("Unique ID: " + document.getFileUniqueId());
        System.out.println("File name: " + document.getFileName());
        System.out.println("File size: " + document.getFileSize());
        System.out.println("MIME type: " + document.getMimeType());

        TelegramMedia telegramMedia = new TelegramMedia();

        telegramMedia.setChatId(Long.parseLong(chatId));
        telegramMedia.setMessageId(message.getMessageId());
        telegramMedia.setFileId(document.getFileId());
        telegramMedia.setFileUniqueId(document.getFileUniqueId());

        return telegramMedia;
    }

    public TelegramFile getFile(String fileId) {

        TelegramResponse<TelegramFile> response = telegramClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getFile")
                        .queryParam("file_id", fileId)
                        .build())
                .retrieve()
                .body(
                        new ParameterizedTypeReference<TelegramResponse<TelegramFile>>() {
                        });

        if (response == null || !response.isOk()) {
            throw new RuntimeException(
                    "Failed to get Telegram file information");
        }

        if (response.getResult() == null) {
            throw new RuntimeException(
                    "Telegram returned no file information");
        }

        return response.getResult();
    }

    public byte[] downloadFile(String filePath) {

        return telegramFileClient
                .get()
                .uri("/" + filePath)
                .retrieve()
                .body(byte[].class);
    }
}
