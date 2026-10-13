package com.akr.swapi.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "swapi")
public record SwapiProperties(
        String host,
        String resource,
        Duration timeout,
        int maxRetries,
        Duration retryBackoff) {
}
