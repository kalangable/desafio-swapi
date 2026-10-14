package com.akr.films.ingestion;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@RequiredArgsConstructor
@Component
public class HttpFetcher {

    private final HttpClientProperties properties;

    public Mono<String> get(WebClient webClient, String uri) {
        log.atDebug().addKeyValue("event", "http_get").addKeyValue("uri", uri).log("GET {}", uri);
        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp -> Mono.error(new RuntimeException("Client error")))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> Mono.error(new RuntimeException("Server error")))
                .bodyToMono(String.class)
                .timeout(properties.timeout())
                .retryWhen(Retry.fixedDelay(properties.maxRetries(), properties.retryBackoff())
                        .doBeforeRetry(signal -> log.atWarn().addKeyValue("event", "http_retry")
                                .addKeyValue("uri", uri).addKeyValue("attempt", signal.totalRetries() + 1)
                                .addKeyValue("maxRetries", properties.maxRetries())
                                .addKeyValue("error", signal.failure().toString())
                                .log("GET {} failed, retry {} of {}", uri, signal.totalRetries() + 1,
                                        properties.maxRetries())))
                .doOnError(e -> log.atError().addKeyValue("event", "http_failed").addKeyValue("uri", uri)
                        .addKeyValue("error", e.toString()).log("GET {} failed for good", uri));
    }

}
