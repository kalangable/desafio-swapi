package com.akr.films.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.akr.films.domain.Film;
import com.akr.films.domain.FilmDetail;
import com.akr.films.domain.FilmSummary;
import com.akr.films.exception.ApiExceptionHandler;
import com.akr.films.exception.FilmNotFoundException;
import com.akr.films.exception.InvalidEpisodeException;
import com.akr.films.mapper.FilmRequestMapperImpl;
import com.akr.films.mapper.OptionalUnwrapperImpl;
import com.akr.films.service.FilmService;

@ExtendWith(MockitoExtension.class)
class FilmControllerTest {

    @Mock
    private FilmService service;

    private RestTestClient client;

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToController(new FilmController(service, new FilmRequestMapperImpl(new OptionalUnwrapperImpl())))
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
                new FilmDetail("A New Hope", "IV", LocalDate.of(1977, 5, 25), "crawl", "1"));

        client.get().uri("/films/4")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("A New Hope")
                .jsonPath("$.episode").isEqualTo("IV")
                .jsonPath("$.releaseDate").isEqualTo("1977-05-25")
                .jsonPath("$.description").isEqualTo("crawl")
                .jsonPath("$.version").isEqualTo("1");
    }

    @Test
    void shouldPassRomanNumeralToService() {
        when(service.findByEpisode("IV")).thenReturn(
                new FilmDetail("A New Hope", "IV", LocalDate.of(1977, 5, 25), "crawl", "1"));

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

    @Test
    void shouldUpdateOnlyTheInformedFields() {
        when(service.update(any(Film.class))).thenReturn(
                new FilmDetail("A New Hope", "IV", LocalDate.of(1977, 5, 25), "new crawl", "2"));

        client.patch().uri("/films/IV")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"description":"new crawl"}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.description").isEqualTo("new crawl")
                .jsonPath("$.version").isEqualTo("2");

        ArgumentCaptor<Film> sent = ArgumentCaptor.forClass(Film.class);
        verify(service).update(sent.capture());
        assertEquals(4, sent.getValue().getEpisode().toInt());
        assertEquals("new crawl", sent.getValue().getDescription());
        assertNull(sent.getValue().getTitle());
        assertNull(sent.getValue().getReleaseDate());
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingWithInvalidEpisode() {
        client.patch().uri("/films/abc")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"description":"crawl"}
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Invalid episode: abc");

        verifyNoInteractions(service);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingFilmThatIsNotLoaded() {
        when(service.update(any(Film.class))).thenThrow(new FilmNotFoundException("V"));

        client.patch().uri("/films/5")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"description":"crawl"}
                        """)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Film not found for episode: V");
    }

}
