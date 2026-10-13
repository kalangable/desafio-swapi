package com.akr.films.source.swapi;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sources.swapi")
public record SwapiProperties(
        String host,
        String resource) {
}
