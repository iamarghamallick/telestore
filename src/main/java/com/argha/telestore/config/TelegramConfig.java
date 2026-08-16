package com.argha.telestore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class TelegramConfig {

    @Bean("telegramRestClient")
    public RestClient telegramRestClient(
            @Value("${telegram.bot-token}") String botToken) {

        return RestClient.builder()
                .baseUrl("https://api.telegram.org/bot" + botToken)
                .build();
    }

    @Bean("telegramFileClient")
    public RestClient telegramFileClient(
            @Value("${telegram.bot-token}") String botToken) {

        return RestClient.builder()
                .baseUrl("https://api.telegram.org/file/bot" + botToken)
                .build();
    }
}
