package com.argha.telestore.dto.media;

import lombok.Data;

@Data
public class UpdateMediaRequest {

    private String folderId = null;
    private String filename = null;
}
