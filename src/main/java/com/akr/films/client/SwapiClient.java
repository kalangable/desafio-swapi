package com.akr.films.client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.akr.films.domain.Episode;
import com.akr.films.domain.Film;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
public class SwapiClient {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    public List<Film> process(String json) {
        List<Film> films = new ArrayList<>();
        for (JsonNode film : jsonMapper.readTree(json).path("results")) {
            String title = film.path("title").asString();
            LocalDate releaseDate = LocalDate.parse(film.path("release_date").asString());
            String description = film.path("opening_crawl").asString();
            Episode episode = new Episode(film.path("episode_id").asInt());
            log.info("{} - episode {} ({})", title, episode, releaseDate);
            films.add(new Film(title, episode, releaseDate, description));
        }
        log.info("{}", films);
        return films;
    }

}
