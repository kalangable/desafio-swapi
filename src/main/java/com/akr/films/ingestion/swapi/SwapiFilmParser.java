package com.akr.films.ingestion.swapi;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.akr.films.domain.Film;
import com.akr.films.domain.Franchise;
import com.akr.films.domain.Identifier;
import com.akr.films.util.RomanNumeral;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
public class SwapiFilmParser {

    private static final String SOURCE = "swapi";
    private static final Franchise FRANCHISE = new Franchise("star-wars", "Star Wars");

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    public List<Film> parse(String json) {
        List<Film> films = new ArrayList<>();
        for (JsonNode film : jsonMapper.readTree(json).path("results")) {
            Map<String, String> extras = new LinkedHashMap<>();
            extras.put("openingCrawl", film.path("opening_crawl").asString());
            extras.put("producer", film.path("producer").asString());
            int episode = film.path("episode_id").asInt();
            Film parsed = Film.builder()
                    .id(new Identifier("star-wars-" + episode))
                    .franchise(FRANCHISE)
                    .title(film.path("title").asString())
                    .label("Episode " + RomanNumeral.toRoman(episode))
                    .releaseDate(LocalDate.parse(film.path("release_date").asString()))
                    .director(film.path("director").asString())
                    .extras(extras)
                    .source(SOURCE)
                    .externalId(film.path("url").asString())
                    .build();
            log.info("{} ({})", parsed.getDisplayTitle(), parsed.getReleaseDate());
            films.add(parsed);
        }
        return films;
    }

}
