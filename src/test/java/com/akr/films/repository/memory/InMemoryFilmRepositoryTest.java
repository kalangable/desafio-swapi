package com.akr.films.repository.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.akr.films.domain.Film;
import com.akr.films.domain.Identifier;

class InMemoryFilmRepositoryTest {

    private final InMemoryFilmRepository repository = new InMemoryFilmRepository();

    private Film.FilmBuilder newHope() {
        return Film.builder()
                .id(new Identifier("star-wars-1"))
                .title("A New Hope")
                .releaseDate(LocalDate.of(1977, 5, 25));
    }

    @Test
    void shouldReturnInsertedFilm() {
        Film film = newHope().build();
        assertEquals(film, repository.insert(film));
    }

    @Test
    void shouldFindInsertedFilmById() {
        Film film = repository.insert(newHope().build());

        assertEquals(Optional.of(film), repository.findById(new Identifier("star-wars-1")));
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {
        assertTrue(repository.findById(new Identifier("missing")).isEmpty());
    }

    @Test
    void shouldFindAllInsertedFilms() {
        Film newHope = repository.insert(newHope().build());
        Film empire = repository.insert(
                Film.builder().id(new Identifier("star-wars-2")).title("The Empire Strikes Back").build());

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
