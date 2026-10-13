package com.akr.swapi.service;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.akr.swapi.config.SwapiProperties;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@RequiredArgsConstructor
@Service
public class SwapiImportService {

    private final WebClient webClient;
    private final SwapiProperties properties;

    public Mono<String> getData() {
        return webClient.get()
                .uri(properties.resource())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp -> Mono.error(new RuntimeException("Client error")))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> Mono.error(new RuntimeException("Server error")))
                .bodyToMono(String.class)
                .timeout(properties.timeout())
                .retryWhen(Retry.fixedDelay(properties.maxRetries(), properties.retryBackoff()));
    }

}
