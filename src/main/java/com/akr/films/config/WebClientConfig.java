package com.akr.films.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration 
public class WebClientConfig {

    @Bean
    public WebClient webClient(SwapiProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.host())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

}
