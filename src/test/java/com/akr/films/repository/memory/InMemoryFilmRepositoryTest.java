package com.akr.films.repository.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.akr.films.domain.Film;

class InMemoryFilmRepositoryTest {

    private final InMemoryFilmRepository repository = new InMemoryFilmRepository();

    private Film.FilmBuilder newHope() {
        return Film.builder().title("A New Hope").releaseDate(LocalDate.of(1977, 5, 25));
    }

    @Test
    void shouldReturnInsertedFilm() {
        Film film = newHope().build();
        assertEquals(film, repository.insert(film));
    }

    @Test
    void shouldGenerateAnIdWhenTheFilmHasNone() {
        Film film = repository.insert(newHope().build());

        assertNotNull(film.getId());
        assertEquals(Optional.of(film), repository.findById(film.getId()));
    }

    @Test
    void shouldKeepTheInformedId() {
        repository.insert(newHope().id("fixed").build());

        assertFalse(repository.findById("fixed").isPresent());
    }

    @Test
    void shouldGenerateDifferentIdsForDifferentFilms() {
        Film first = repository.insert(newHope().build());
        Film second = repository.insert(newHope().build());

        assertNotEquals(first.getId(), second.getId());
        assertEquals(2, repository.findAll().size());
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {
        assertTrue(repository.findById("missing").isEmpty());
    }

    @Test
    void shouldFindAllInsertedFilms() {
        Film newHope = repository.insert(newHope().build());
        Film empire = repository.insert(Film.builder().title("The Empire Strikes Back").build());

        assertEquals(2, repository.findAll().size());
        assertTrue(repository.findAll().contains(newHope));
        assertTrue(repository.findAll().contains(empire));
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoFilms() {
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void shouldReplaceFilmOnUpdate() {
        Film inserted = repository.insert(newHope().build());
        Film updated = inserted.toBuilder().synopsis("new synopsis").build();

        assertEquals(updated, repository.update(updated));
        assertEquals(Optional.of(updated), repository.findById(inserted.getId()));
    }

}
