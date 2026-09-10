package com.argha.telestore.dto.media;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateMediaRequest {

    private String folderId;

    @NotBlank(message = "File name is required")
    private String filename;
}
