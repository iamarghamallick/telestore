package com.argha.telestore.dto.folder;

import lombok.Data;

@Data
public class UpdateFolderRequest {

    private String name;
    private String parentFolderId;
}
