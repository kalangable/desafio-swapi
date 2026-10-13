package com.akr.swapi.repository.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Optional;

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

    @Test
    void shouldFindInsertedFilmByEpisodeNumber() {
        Film film = new Film("A New Hope", new Episode(4), LocalDate.of(1977, 5, 25), "crawl");
        repository.insert(film);
        assertEquals(Optional.of(film), repository.findById(4));
    }

    @Test
    void shouldReturnEmptyWhenEpisodeDoesNotExist() {
        assertTrue(repository.findById(4).isEmpty());
    }

}
