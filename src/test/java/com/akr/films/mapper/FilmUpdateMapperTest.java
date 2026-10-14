package com.akr.films.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.akr.films.domain.Identifier;
import com.akr.films.domain.Film;
import com.akr.films.domain.Version;

class FilmUpdateMapperTest {

    private final FilmUpdateMapper mapper = new FilmUpdateMapperImpl();

    private final Film current = Film.builder()
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
            .version(new Version(1))
            .build();

    @Test
    void shouldReplaceOnlyTheFieldsThatAreNotNull() {
        mapper.apply(Film.builder().synopsis("new synopsis").build(), current);

        assertEquals("A New Hope", current.getTitle());
        assertEquals(LocalDate.of(1977, 5, 25), current.getReleaseDate());
        assertEquals("new synopsis", current.getSynopsis());
        assertEquals(121, current.getRuntimeMinutes());
        assertEquals("George Lucas", current.getDirector());
    }

    @Test
    void shouldReplaceAllEditableFieldsWhenNoneIsNull() {
        Film changes = Film.builder().title("Episode IV").releaseDate(LocalDate.of(1978, 1, 1))
                .synopsis("new synopsis").runtimeMinutes(120).director("Lucas").build();

        mapper.apply(changes, current);

        assertEquals("Episode IV", current.getTitle());
        assertEquals(LocalDate.of(1978, 1, 1), current.getReleaseDate());
        assertEquals("new synopsis", current.getSynopsis());
        assertEquals(120, current.getRuntimeMinutes());
        assertEquals("Lucas", current.getDirector());
    }

    @Test
    void shouldChangeNothingWhenAllFieldsAreNull() {
        Film before = current.toBuilder().build();

        mapper.apply(new Film(), current);

        assertEquals(before, current);
    }

    @Test
    void shouldNeverChangeTheFieldsThatCannotBeEdited() {
        Film changes = Film.builder().id(new Identifier("2")).label("Episode V").extras(Map.of("k", "v")).source("other")
                .externalId("other").version(new Version(9)).build();

        mapper.apply(changes, current);

        assertEquals(new Identifier("1"), current.getId());
        assertEquals("Episode IV", current.getLabel());
        assertEquals(Map.of("openingCrawl", "crawl"), current.getExtras());
        assertEquals("swapi", current.getSource());
        assertEquals("https://swapi.dev/api/films/1/", current.getExternalId());
        assertEquals(new Version(1), current.getVersion());
    }

}
