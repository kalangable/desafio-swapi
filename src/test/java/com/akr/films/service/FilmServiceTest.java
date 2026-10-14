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
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.akr.films.domain.Film;
import com.akr.films.domain.FilmDetail;
import com.akr.films.domain.FilmSummary;
import com.akr.films.domain.Version;
import com.akr.films.exception.FilmNotFoundException;
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

    private Film newHope() {
        return Film.builder()
                .id("1")
                .title("A New Hope")
                .label("Episode IV")
                .releaseDate(LocalDate.of(1977, 5, 25))
                .synopsis("synopsis")
                .extras(Map.of("openingCrawl", "crawl"))
                .source("swapi")
                .version(new Version(1))
                .build();
    }

    @Test
    void shouldFindAllAsSummaries() {
        when(repository.findAll()).thenReturn(List.of(newHope(), Film.builder().id("2").title("Heat").build()));

        assertEquals(List.of(
                new FilmSummary("1", "Episode IV - A New Hope"),
                new FilmSummary("2", "Heat")), service.findAll());
    }

    @Test
    void shouldReturnEmptyListWhenThereIsNoData() {
        when(repository.findAll()).thenReturn(List.of());

        assertTrue(assertDoesNotThrow(() -> service.findAll()).isEmpty());
    }

    @Test
    void shouldFindById() {
        when(repository.findById("1")).thenReturn(Optional.of(newHope()));

        FilmDetail detail = service.findById("1");

        assertEquals("A New Hope", detail.title());
        assertEquals("Episode IV - A New Hope", detail.displayTitle());
        assertEquals(Map.of("openingCrawl", "crawl"), detail.extras());
    }

    @Test
    void shouldFailWhenIdIsNotLoaded() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(FilmNotFoundException.class, () -> service.findById("x"));
    }

    @Test
    void shouldChangeOnlyInformedFieldsAndIncrementTheVersion() {
        Film current = newHope();
        Film changes = Film.builder().id("1").synopsis("new synopsis").runtimeMinutes(121).build();
        when(repository.findById("1")).thenReturn(Optional.of(current));
        when(updater.update(any(Film.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FilmDetail detail = service.update(changes);

        assertEquals("A New Hope", detail.title());
        assertEquals("Episode IV - A New Hope", detail.displayTitle());
        assertEquals("new synopsis", detail.synopsis());
        assertEquals(121, detail.runtimeMinutes());
        assertEquals("2", detail.version());
    }

    @Test
    void shouldKeepTheLabelWhenTheTitleChanges() {
        when(repository.findById("1")).thenReturn(Optional.of(newHope()));
        when(updater.update(any(Film.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FilmDetail detail = service.update(Film.builder().id("1").title("A Good Hope").build());

        assertEquals("Episode IV - A Good Hope", detail.displayTitle());
    }

    @Test
    void shouldNotSaveNorIncrementTheVersionWhenNothingChanges() {
        when(repository.findById("1")).thenReturn(Optional.of(newHope()));

        FilmDetail detail = service.update(Film.builder().id("1").title("A New Hope").build());

        assertEquals("1", detail.version());
        verifyNoInteractions(updater);
    }

    @Test
    void shouldNotIncrementTheVersionWhenNoFieldIsInformed() {
        when(repository.findById("1")).thenReturn(Optional.of(newHope()));

        FilmDetail detail = service.update(Film.builder().id("1").build());

        assertEquals("1", detail.version());
        verifyNoInteractions(updater);
    }

    @Test
    void shouldSaveOnlyOnceWhenTheSameChangeIsSentRepeatedly() {
        Film current = newHope();
        Film changes = Film.builder().id("1").title("A Good Hope").build();
        when(repository.findById("1")).thenReturn(Optional.of(current));
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
        Film changes = Film.builder().id("x").synopsis("synopsis").build();
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(FilmNotFoundException.class, () -> service.update(changes));
        verifyNoInteractions(updater);
    }

}
