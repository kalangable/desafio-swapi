package com.akr.films.ingestion;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@RequiredArgsConstructor
@Component
public class HttpFetcher {

    private final HttpClientProperties properties;

    public Mono<String> get(WebClient webClient, String uri) {
        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp -> Mono.error(new RuntimeException("Client error")))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> Mono.error(new RuntimeException("Server error")))
                .bodyToMono(String.class)
                .timeout(properties.timeout())
                .retryWhen(Retry.fixedDelay(properties.maxRetries(), properties.retryBackoff()));
    }

}
