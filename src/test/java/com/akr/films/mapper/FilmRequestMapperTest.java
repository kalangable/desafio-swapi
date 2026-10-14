package com.akr.films.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.akr.films.domain.Identifier;
import com.akr.films.controller.FilmRequestUpdate;
import com.akr.films.domain.Film;

class FilmRequestMapperTest {

    private final FilmRequestMapper mapper = new FilmRequestMapperImpl(new OptionalUnwrapperImpl());

    @Test
    void shouldUnwrapThePresentFieldsAndKeepTheId() {
        FilmRequestUpdate request = new FilmRequestUpdate(Optional.of("A New Hope"),
                Optional.of(LocalDate.of(1977, 5, 25)), Optional.of("synopsis"), Optional.of(121),
                Optional.of("George Lucas"));

        Film film = mapper.toFilm("1", request);

        assertEquals(new Identifier("1"), film.getId());
        assertEquals("A New Hope", film.getTitle());
        assertEquals(LocalDate.of(1977, 5, 25), film.getReleaseDate());
        assertEquals("synopsis", film.getSynopsis());
        assertEquals(121, film.getRuntimeMinutes());
        assertEquals("George Lucas", film.getDirector());
    }

    @Test
    void shouldLeaveAbsentFieldsAsNull() {
        FilmRequestUpdate request = new FilmRequestUpdate(Optional.empty(), Optional.empty(),
                Optional.of("new synopsis"), Optional.empty(), Optional.empty());

        Film film = mapper.toFilm("1", request);

        assertNull(film.getTitle());
        assertNull(film.getReleaseDate());
        assertNull(film.getRuntimeMinutes());
        assertNull(film.getDirector());
        assertEquals("new synopsis", film.getSynopsis());
        assertEquals(new Identifier("1"), film.getId());
    }

    @Test
    void shouldNotFillTheFieldsThatCannotBeEdited() {
        Film film = mapper.toFilm("1", new FilmRequestUpdate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty()));

        assertNull(film.getFranchise());
        assertNull(film.getLabel());
        assertNull(film.getSource());
        assertNull(film.getExternalId());
        assertTrue(film.getExtras().isEmpty());
    }

}
