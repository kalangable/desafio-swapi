package com.akr.films.ingestion;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "http-client")
public record HttpClientProperties(
        Duration timeout,
        int maxRetries,
        Duration retryBackoff) {
}
