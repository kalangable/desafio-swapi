package com.akr.films.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.akr.films.domain.Episode;
import com.akr.films.domain.Film;
import com.akr.films.domain.FilmDetail;
import com.akr.films.domain.FilmSummary;
import com.akr.films.domain.Version;
import com.akr.films.exception.FilmNotFoundException;
import com.akr.films.exception.InvalidEpisodeException;
import com.akr.films.mapper.FilmMapperImpl;
import com.akr.films.mapper.FilmUpdateMapperImpl;
import com.akr.films.repository.FilmFindable;
import com.akr.films.repository.FilmUpdatable;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    private FilmFindable repository;

    @Mock
    private FilmUpdatable updater;

    private FilmService service;

    @BeforeEach
    void setUp() {
        service = new FilmService(repository, updater, new FilmMapperImpl(),
                new FilmUpdateMapperImpl());
    }

    @Test
    void shouldFindAllAsSummaries() {
        when(repository.findAll()).thenReturn(List.of(
                new Film("A New Hope", new Episode(4), LocalDate.of(1977, 5, 25), "crawl"),
                new Film("The Empire Strikes Back", new Episode(5), LocalDate.of(1980, 5, 17), "crawl")));

        assertEquals(List.of(
                new FilmSummary("A New Hope", "IV"),
                new FilmSummary("The Empire Strikes Back", "V")), service.findAll());
    }

    @Test
    void shouldReturnEmptyListWhenThereIsNoData() {
        when(repository.findAll()).thenReturn(List.of());

        assertTrue(assertDoesNotThrow(() -> service.findAll()).isEmpty());
    }

    @Test
    void shouldFindByNumberOrRoman() {
        Film film = new Film("A New Hope", new Episode(4), LocalDate.of(1977, 5, 25), "crawl");
        when(repository.findById(4)).thenReturn(Optional.of(film));

        assertEquals("A New Hope", service.findByEpisode("4").title());
        assertEquals("A New Hope", service.findByEpisode("IV").title());
        assertEquals("A New Hope", service.findByEpisode("iv").title());
        verify(repository, times(3)).findById(4);
    }

    @Test
    void shouldFailWhenEpisodeIsValidButNotLoaded() {
        when(repository.findById(5)).thenReturn(Optional.empty());

        assertThrows(FilmNotFoundException.class, () -> service.findByEpisode("5"));
    }

    @Test
    void shouldFailWithoutQueryingWhenEpisodeIsInvalid() {
        assertThrows(InvalidEpisodeException.class, () -> service.findByEpisode("abc"));
        verifyNoInteractions(repository);
    }

    @Test
    void shouldChangeOnlyInformedFieldsAndIncrementTheVersion() {
        Film current = new Film("A New Hope", new Episode(4), LocalDate.of(1977, 5, 25), "crawl", new Version(1));
        Film changes = new Film(null, new Episode(4), null, "new crawl", null);
        when(repository.findById(4)).thenReturn(Optional.of(current));
        when(updater.update(any(Film.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FilmDetail detail = service.update(changes);

        assertEquals(new FilmDetail("A New Hope", "IV", LocalDate.of(1977, 5, 25), "new crawl", "2"), detail);
    }

    @Test
    void shouldNotSaveNorIncrementTheVersionWhenNothingChanges() {
        Film current = new Film("A New Hope", new Episode(4), LocalDate.of(1977, 5, 25), "crawl", new Version(1));
        Film changes = new Film("A New Hope", new Episode(4), null, null, null);
        when(repository.findById(4)).thenReturn(Optional.of(current));

        FilmDetail detail = service.update(changes);

        assertEquals(new FilmDetail("A New Hope", "IV", LocalDate.of(1977, 5, 25), "crawl", "1"), detail);
        verifyNoInteractions(updater);
    }

    @Test
    void shouldNotIncrementTheVersionWhenNoFieldIsInformed() {
        Film current = new Film("A New Hope", new Episode(4), LocalDate.of(1977, 5, 25), "crawl", new Version(1));
        when(repository.findById(4)).thenReturn(Optional.of(current));

        FilmDetail detail = service.update(new Film(null, new Episode(4), null, null, null));

        assertEquals("1", detail.version());
        verifyNoInteractions(updater);
    }

    @Test
    void shouldSaveOnlyOnceWhenTheSameChangeIsSentRepeatedly() {
        Film current = new Film("A New Hope", new Episode(4), LocalDate.of(1977, 5, 25), "crawl", new Version(1));
        Film changes = new Film("A Good Hope", new Episode(4), null, null, null);
        when(repository.findById(4)).thenReturn(Optional.of(current));
        when(updater.update(any(Film.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.update(changes);
        service.update(changes);
        FilmDetail last = service.update(changes);

        assertEquals("A Good Hope", last.title());
        assertEquals("2", last.version());
        verify(updater, times(1)).update(any(Film.class));
    }

    @Test
    void shouldFailUpdateWithoutUpdatingWhenFilmIsNotLoaded() {
        Film changes = new Film(null, new Episode(5), null, "crawl", null);
        when(repository.findById(5)).thenReturn(Optional.empty());

        assertThrows(FilmNotFoundException.class, () -> service.update(changes));
        verifyNoInteractions(updater);
    }

}
