package com.akr.swapi.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.akr.swapi.domain.FilmDetail;
import com.akr.swapi.domain.FilmSummary;
import com.akr.swapi.exception.ApiExceptionHandler;
import com.akr.swapi.exception.FilmNotFoundException;
import com.akr.swapi.exception.InvalidEpisodeException;
import com.akr.swapi.service.SwapiFilmService;

@ExtendWith(MockitoExtension.class)
class SwapiControllerTest {

    @Mock
    private SwapiFilmService service;

    private RestTestClient client;

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToController(new SwapiController(service))
                .configureServer(server -> server.setControllerAdvice(new ApiExceptionHandler()))
                .build();
    }

    @Test
    void shouldListAllFilms() {
        when(service.findAll()).thenReturn(List.of(
                new FilmSummary("A New Hope", "IV"),
                new FilmSummary("The Empire Strikes Back", "V")));

        client.get().uri("/films")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].title").isEqualTo("A New Hope")
                .jsonPath("$[0].episode").isEqualTo("IV")
                .jsonPath("$[1].episode").isEqualTo("V");
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoFilms() {
        when(service.findAll()).thenReturn(List.of());

        client.get().uri("/films")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    void shouldReturnFilmDetailByNumber() {
        when(service.findByEpisode("4")).thenReturn(
                new FilmDetail("A New Hope", "IV", LocalDate.of(1977, 5, 25), "crawl"));

        client.get().uri("/films/4")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("A New Hope")
                .jsonPath("$.episode").isEqualTo("IV")
                .jsonPath("$.releaseDate").isEqualTo("1977-05-25")
                .jsonPath("$.description").isEqualTo("crawl");
    }

    @Test
    void shouldPassRomanNumeralToService() {
        when(service.findByEpisode("IV")).thenReturn(
                new FilmDetail("A New Hope", "IV", LocalDate.of(1977, 5, 25), "crawl"));

        client.get().uri("/films/IV")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.episode").isEqualTo("IV");
        verify(service).findByEpisode("IV");
    }

    @Test
    void shouldReturnBadRequestProblemDetailForInvalidEpisode() {
        when(service.findByEpisode("abc")).thenThrow(new InvalidEpisodeException("abc"));

        client.get().uri("/films/abc")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.detail").isEqualTo("Invalid episode: abc");
    }

    @Test
    void shouldReturnNotFoundProblemDetailWhenFilmDoesNotExist() {
        when(service.findByEpisode("5")).thenThrow(new FilmNotFoundException("V"));

        client.get().uri("/films/5")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.detail").isEqualTo("Film not found for episode: V");
    }

}
