package com.argha.telestore.dto.media;

import lombok.Data;

@Data
public class UpdateMediaRequest {

    private String folderId;

    private String filename;
}
