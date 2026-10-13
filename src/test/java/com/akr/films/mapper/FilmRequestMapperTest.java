package com.akr.swapi.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.akr.swapi.controller.FilmRequestUpdate;
import com.akr.swapi.domain.Film;
import com.akr.swapi.exception.InvalidEpisodeException;

class FilmRequestMapperTest {

    private final FilmRequestMapper mapper = new FilmRequestMapperImpl(new OptionalUnwrapperImpl());

    @Test
    void shouldUnwrapThePresentFieldsAndParseTheEpisode() {
        FilmRequestUpdate request = new FilmRequestUpdate(
                Optional.of("A New Hope"), Optional.of(LocalDate.of(1977, 5, 25)), Optional.of("crawl"));

        Film film = mapper.toFilm("IV", request);

        assertEquals("A New Hope", film.getTitle());
        assertEquals(4, film.getEpisode().toInt());
        assertEquals(LocalDate.of(1977, 5, 25), film.getReleaseDate());
        assertEquals("crawl", film.getDescription());
    }

    @Test
    void shouldLeaveAbsentFieldsAsNull() {
        FilmRequestUpdate request = new FilmRequestUpdate(Optional.empty(), Optional.empty(), Optional.of("new crawl"));

        Film film = mapper.toFilm("4", request);

        assertNull(film.getTitle());
        assertNull(film.getReleaseDate());
        assertEquals("new crawl", film.getDescription());
        assertEquals(4, film.getEpisode().toInt());
    }

    @Test
    void shouldRejectInvalidEpisode() {
        FilmRequestUpdate request = new FilmRequestUpdate(Optional.empty(), Optional.empty(), Optional.of("crawl"));

        assertThrows(InvalidEpisodeException.class, () -> mapper.toFilm("abc", request));
    }

}
