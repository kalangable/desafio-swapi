package com.akr.films.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.akr.films.domain.Film;
import com.akr.films.domain.FilmSummary;
import com.akr.films.domain.Franchise;
import com.akr.films.domain.Identifier;
import com.akr.films.domain.PageInfo;
import com.akr.films.domain.PageQuery;
import com.akr.films.domain.PageResponse;
import com.akr.films.exception.FranchiseNotFoundException;
import com.akr.films.mapper.FilmMapperImpl;
import com.akr.films.repository.FranchiseFindable;

@ExtendWith(MockitoExtension.class)
class FranchiseServiceTest {

    private static final Franchise STAR_WARS = new Franchise("star-wars", "Star Wars");

    @Mock
    private FranchiseFindable repository;

    private FranchiseService service;

    @BeforeEach
    void setUp() {
        service = new FranchiseService(repository, new FilmMapperImpl());
    }

    @Test
    void shouldListTheFranchises() {
        when(repository.findAllFranchises()).thenReturn(List.of(STAR_WARS));

        assertEquals(List.of(STAR_WARS), service.findAll());
    }

    @Test
    void shouldFindThePageOfFilmsOfAFranchiseAsSummaries() {
        PageQuery query = new PageQuery(0, 2);
        Film film = Film.builder().id(new Identifier("star-wars-4")).franchise(STAR_WARS).title("A New Hope")
                .label("Episode IV").build();
        when(repository.findFranchiseBySlug("star-wars")).thenReturn(Optional.of(STAR_WARS));
        when(repository.findByFranchise("star-wars", query)).thenReturn(PageResponse.of(List.of(film), query, 1));

        PageResponse<FilmSummary> page = service.findFilms("star-wars", query);

        assertEquals(List.of(new FilmSummary("star-wars-4", "Episode IV - A New Hope", "Star Wars")), page.content());
        assertEquals(new PageInfo(2, 0, 1, 1), page.page());
    }

    @Test
    void shouldFailWhenTheFranchiseDoesNotExist() {
        PageQuery query = new PageQuery(0, 2);
        when(repository.findFranchiseBySlug("unknown")).thenReturn(Optional.empty());

        FranchiseNotFoundException e = assertThrows(FranchiseNotFoundException.class,
                () -> service.findFilms("unknown", query));

        assertEquals("Franchise not found: unknown", e.getMessage());
        verify(repository, never()).findByFranchise("unknown", query);
    }

}
