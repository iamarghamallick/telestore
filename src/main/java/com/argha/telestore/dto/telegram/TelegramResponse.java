package com.argha.telestore.dto.telegram;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelegramResponse<T> {

    private boolean ok;
    private T result;
    private String description;
}
