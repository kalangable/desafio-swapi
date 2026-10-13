package com.akr.swapi.repository.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

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

    @Test
    void shouldFindAllInsertedFilms() {
        Film newHope = new Film("A New Hope", new Episode(4), LocalDate.of(1977, 5, 25), "crawl");
        Film empire = new Film("The Empire Strikes Back", new Episode(5), LocalDate.of(1980, 5, 17), "crawl");
        repository.insert(newHope);
        repository.insert(empire);
        assertEquals(2, repository.findAll().size());
        assertTrue(repository.findAll().contains(newHope));
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoFilms() {
        assertTrue(repository.findAll().isEmpty());
    }

}
