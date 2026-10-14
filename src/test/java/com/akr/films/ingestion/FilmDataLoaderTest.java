package com.akr.films.ingestion;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.akr.films.domain.Film;
import com.akr.films.domain.Identifier;
import com.akr.films.repository.FilmCreation;
import com.akr.films.repository.FilmFindable;

class FilmDataLoaderTest {

    private final FilmCreation creator = mock(FilmCreation.class);

    private final FilmFindable finder = mock(FilmFindable.class);

    private final FilmSource swapi = mock(FilmSource.class);

    private final FilmSource other = mock(FilmSource.class);

    private FilmDataLoader loader(FilmSource... sources) {
        return new FilmDataLoader(List.of(sources), creator, finder);
    }

    private Film film(String id) {
        return Film.builder().id(new Identifier(id)).title(id).build();
    }

    @Test
    void shouldInsertEveryFilmOfEverySource() {
        Film first = film("star-wars-4");
        Film second = film("star-wars-5");
        Film third = film("fast-and-furious-1");
        when(swapi.fetch()).thenReturn(List.of(first, second));
        when(other.fetch()).thenReturn(List.of(third));
        when(finder.findById(any(Identifier.class))).thenReturn(Optional.empty());

        loader(swapi, other).run(null);

        verify(creator).insert(first);
        verify(creator).insert(second);
        verify(creator).insert(third);
    }

    @Test
    void shouldSkipFilmWhoseIdAlreadyExistsAndKeepLoading() {
        Film existing = film("star-wars-4");
        Film next = film("star-wars-5");
        when(swapi.fetch()).thenReturn(List.of(existing, next));
        when(finder.findById(new Identifier("star-wars-4"))).thenReturn(Optional.of(existing));
        when(finder.findById(new Identifier("star-wars-5"))).thenReturn(Optional.empty());

        loader(swapi).run(null);

        verify(creator, never()).insert(existing);
        verify(creator).insert(next);
    }

    @Test
    void shouldSkipFilmWithoutIdAndKeepLoading() {
        Film noId = Film.builder().title("No id").build();
        Film next = film("star-wars-5");
        when(swapi.fetch()).thenReturn(List.of(noId, next));
        when(finder.findById(any(Identifier.class))).thenReturn(Optional.empty());

        loader(swapi).run(null);

        verify(creator, never()).insert(noId);
        verify(creator).insert(next);
    }

    @Test
    void shouldKeepGoingWhenASourceFails() {
        Film film = film("fast-and-furious-1");
        when(swapi.fetch()).thenThrow(new RuntimeException("boom"));
        when(other.fetch()).thenReturn(List.of(film));
        when(finder.findById(any(Identifier.class))).thenReturn(Optional.empty());

        loader(swapi, other).run(null);

        verify(creator).insert(film);
    }

    @Test
    void shouldInsertNothingWhenThereAreNoSources() {
        loader().run(null);

        verify(creator, never()).insert(any(Film.class));
    }

}
