package com.akr.films.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.akr.films.domain.Identifier;
import com.akr.films.domain.Film;
import com.akr.films.domain.FilmDetail;
import com.akr.films.domain.FilmSummary;
import com.akr.films.domain.Version;

class FilmMapperTest {

    private final FilmMapper mapper = new FilmMapperImpl();

    private final Film film = Film.builder()
            .id(new Identifier("1"))
            .title("A New Hope")
            .label("Episode IV")
            .releaseDate(LocalDate.of(1977, 5, 25))
            .synopsis("synopsis")
            .runtimeMinutes(121)
            .director("George Lucas")
            .extras(Map.of("openingCrawl", "crawl"))
            .source("swapi")
            .externalId("https://swapi.dev/api/films/1/")
            .version(new Version(3))
            .build();

    @Test
    void shouldMapSummaryUsingIdAndDisplayTitle() {
        assertEquals(new FilmSummary("1", "Episode IV - A New Hope"), mapper.toSummary(film));
    }

    @Test
    void shouldMapDetailWithDisplayTitleExtrasAndVersionAsText() {
        FilmDetail expected = new FilmDetail("1", "A New Hope", "Episode IV - A New Hope", LocalDate.of(1977, 5, 25),
                "synopsis", 121, "George Lucas", Map.of("openingCrawl", "crawl"), "swapi", "3");
        assertEquals(expected, mapper.toDetail(film));
    }

}
