package com.argha.telestore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelegramMedia {

    private Long chatId;
    private Long messageId;
    private String fileId;
    private String fileUniqueId;
}
