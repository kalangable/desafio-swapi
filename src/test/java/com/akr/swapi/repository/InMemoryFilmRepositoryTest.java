package com.akr.swapi.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.akr.swapi.domain.Episode;
import com.akr.swapi.domain.Film;

class InMemoryFilmRepositoryTest {

    private final InMemoryFilmRepository repository = new InMemoryFilmRepository();

    @Test
    void shouldReturnInsertedFilm() {
        Film film = new Film("A New Hope", new Episode(4), LocalDate.of(1977, 5, 25), "crawl");
        assertEquals(film, repository.insert(film));
    }

}
