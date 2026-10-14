package com.akr.films.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.akr.films.domain.FilmSummary;
import com.akr.films.domain.Franchise;
import com.akr.films.domain.PageQuery;
import com.akr.films.domain.PageResponse;
import com.akr.films.exception.ApiExceptionHandler;
import com.akr.films.exception.FranchiseNotFoundException;
import com.akr.films.service.FranchiseService;

@ExtendWith(MockitoExtension.class)
class FranchiseControllerTest {

    private static final Franchise STAR_WARS = new Franchise("star-wars", "Star Wars");

    @Mock
    private FranchiseService service;

    private RestTestClient client;

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToController(new FranchiseController(service, new PaginationProperties(0, 2, 100)))
                .configureServer(server -> server.setControllerAdvice(new ApiExceptionHandler()))
                .build();
    }

    @Test
    void shouldListTheFranchises() {
        when(service.findAll()).thenReturn(List.of(STAR_WARS, new Franchise("fast-and-furious", "Fast & Furious")));

        client.get().uri("/franchises")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].slug").isEqualTo("star-wars")
                .jsonPath("$[0].name").isEqualTo("Star Wars")
                .jsonPath("$[1].slug").isEqualTo("fast-and-furious");
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoFranchises() {
        when(service.findAll()).thenReturn(List.of());

        client.get().uri("/franchises")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    void shouldListTheDefaultPageOfTheFilmsOfAFranchise() {
        PageQuery query = new PageQuery(0, 2);
        when(service.findFilms("star-wars", query)).thenReturn(PageResponse.of(List.of(
                new FilmSummary("star-wars-4", "Episode IV - A New Hope", "Star Wars"),
                new FilmSummary("star-wars-5", "Episode V - The Empire Strikes Back", "Star Wars")), query, 3));

        client.get().uri("/franchises/star-wars/films")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo(2)
                .jsonPath("$.content[0].id").isEqualTo("star-wars-4")
                .jsonPath("$.content[0].franchise").isEqualTo("Star Wars")
                .jsonPath("$.page.totalElements").isEqualTo(3)
                .jsonPath("$.page.totalPages").isEqualTo(2);
    }

    @Test
    void shouldPassTheInformedPageAndSizeToTheService() {
        PageQuery query = new PageQuery(1, 5);
        when(service.findFilms("star-wars", query)).thenReturn(PageResponse.of(List.of(), query, 3));

        client.get().uri("/franchises/star-wars/films?page=1&size=5")
                .exchange()
                .expectStatus().isOk();
        verify(service).findFilms("star-wars", query);
    }

    @Test
    void shouldReturnNotFoundWhenTheFranchiseDoesNotExist() {
        when(service.findFilms("unknown", new PageQuery(0, 2))).thenThrow(new FranchiseNotFoundException("unknown"));

        client.get().uri("/franchises/unknown/films")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.detail").isEqualTo("Franchise not found: unknown");
    }

    @Test
    void shouldReturnBadRequestWhenThePaginationIsInvalid() {
        client.get().uri("/franchises/star-wars/films?size=0")
                .exchange()
                .expectStatus().isBadRequest();
        verifyNoInteractions(service);
    }

}
