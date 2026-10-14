package com.akr.films.ingestion.swapi;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sources.swapi")
public record SwapiProperties(
        String host,
        String resource) {
}
