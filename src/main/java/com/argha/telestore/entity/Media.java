package com.argha.telestore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
@AccessType(AccessType.Type.FIELD)
public class Media {

    @Id
    private String id;

    private String userId;
    private String folderId = null;
    private String filename;
    private MediaType mediaType;
    private String mimeType;
    private Long size;
    private String extension;
    private TelegramMedia media;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
