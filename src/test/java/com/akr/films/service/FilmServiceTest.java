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

import com.akr.films.domain.Identifier;
import com.akr.films.domain.PageInfo;
import com.akr.films.domain.PageQuery;
import com.akr.films.domain.PageResponse;
import com.akr.films.domain.Film;
import com.akr.films.domain.FilmDetail;
import com.akr.films.domain.FilmSummary;
import com.akr.films.domain.Franchise;
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
                .id(new Identifier("1"))
                .franchise(new Franchise("star-wars", "Star Wars"))
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
    void shouldFindAPageAsSummaries() {
        PageQuery query = new PageQuery(0, 2);
        List<Film> films = List.of(newHope(), Film.builder().id(new Identifier("2")).title("Heat").build());
        when(repository.findAll(query)).thenReturn(PageResponse.of(films, query, 5));

        PageResponse<FilmSummary> page = service.findAll(query);

        assertEquals(List.of(
                new FilmSummary("1", "Episode IV - A New Hope", "Star Wars", "1"),
                new FilmSummary("2", "Heat", null, "1")), page.content());
        assertEquals(new PageInfo(2, 0, 5, 3), page.page());
    }

    @Test
    void shouldReturnEmptyPageWhenThereIsNoData() {
        PageQuery query = new PageQuery(0, 2);
        when(repository.findAll(query)).thenReturn(PageResponse.of(List.of(), query, 0));

        assertTrue(assertDoesNotThrow(() -> service.findAll(query)).content().isEmpty());
    }

    @Test
    void shouldFindById() {
        when(repository.findById(new Identifier("1"))).thenReturn(Optional.of(newHope()));

        FilmDetail detail = service.findById(new Identifier("1"));

        assertEquals("A New Hope", detail.title());
        assertEquals("Episode IV - A New Hope", detail.displayTitle());
        assertEquals(Map.of("openingCrawl", "crawl"), detail.extras());
    }

    @Test
    void shouldFailWhenIdIsNotLoaded() {
        when(repository.findById(new Identifier("x"))).thenReturn(Optional.empty());

        assertThrows(FilmNotFoundException.class, () -> service.findById(new Identifier("x")));
    }

    @Test
    void shouldChangeOnlyInformedFieldsAndIncrementTheVersion() {
        Film current = newHope();
        Film changes = Film.builder().id(new Identifier("1")).synopsis("new synopsis").runtimeMinutes(121).build();
        when(repository.findById(new Identifier("1"))).thenReturn(Optional.of(current));
        when(updater.update(any(Film.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FilmDetail detail = service.update(changes);

        assertEquals("A New Hope", detail.title());
        assertEquals("Episode IV - A New Hope", detail.displayTitle());
        assertEquals("new synopsis", detail.synopsis());
        assertEquals(121, detail.runtimeMinutes());
        assertEquals("2", detail.version());
    }

    @Test
    void shouldRemoveLineBreaksFromTheSynopsisWhenUpdating() {
        when(repository.findById(new Identifier("1"))).thenReturn(Optional.of(newHope()));
        when(updater.update(any(Film.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FilmDetail detail = service.update(
                Film.builder().id(new Identifier("1")).synopsis("first line\r\nsecond line\n\nthird").build());

        assertEquals("first line second line third", detail.synopsis());
        assertEquals("2", detail.version());
    }

    @Test
    void shouldKeepTheLabelWhenTheTitleChanges() {
        when(repository.findById(new Identifier("1"))).thenReturn(Optional.of(newHope()));
        when(updater.update(any(Film.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FilmDetail detail = service.update(Film.builder().id(new Identifier("1")).title("A Good Hope").build());

        assertEquals("Episode IV - A Good Hope", detail.displayTitle());
    }

    @Test
    void shouldNotSaveNorIncrementTheVersionWhenNothingChanges() {
        when(repository.findById(new Identifier("1"))).thenReturn(Optional.of(newHope()));

        FilmDetail detail = service.update(Film.builder().id(new Identifier("1")).title("A New Hope").build());

        assertEquals("1", detail.version());
        verifyNoInteractions(updater);
    }

    @Test
    void shouldNotIncrementTheVersionWhenNoFieldIsInformed() {
        when(repository.findById(new Identifier("1"))).thenReturn(Optional.of(newHope()));

        FilmDetail detail = service.update(Film.builder().id(new Identifier("1")).build());

        assertEquals("1", detail.version());
        verifyNoInteractions(updater);
    }

    @Test
    void shouldSaveOnlyOnceWhenTheSameChangeIsSentRepeatedly() {
        Film current = newHope();
        Film changes = Film.builder().id(new Identifier("1")).title("A Good Hope").build();
        when(repository.findById(new Identifier("1"))).thenReturn(Optional.of(current));
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
        Film changes = Film.builder().id(new Identifier("x")).synopsis("synopsis").build();
        when(repository.findById(new Identifier("x"))).thenReturn(Optional.empty());

        assertThrows(FilmNotFoundException.class, () -> service.update(changes));
        verifyNoInteractions(updater);
    }

}
