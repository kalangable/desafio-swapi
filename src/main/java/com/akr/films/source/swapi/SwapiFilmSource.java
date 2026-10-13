package com.akr.films.source.swapi;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.akr.films.domain.Film;
import com.akr.films.source.HttpFetcher;
import com.akr.films.source.FilmSource;

@Component
public class SwapiFilmSource implements FilmSource {

    private final WebClient webClient;
    private final SwapiProperties properties;
    private final HttpFetcher httpFetcher;
    private final SwapiFilmParser parser;

    public SwapiFilmSource(SwapiProperties properties, HttpFetcher httpFetcher, SwapiFilmParser parser) {
        this.properties = properties;
        this.httpFetcher = httpFetcher;
        this.parser = parser;
        this.webClient = WebClient.builder()
                .baseUrl(properties.host())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public List<Film> fetch() {
        String json = httpFetcher.get(webClient, properties.resource()).block();
        return parser.parse(json);
    }

}
