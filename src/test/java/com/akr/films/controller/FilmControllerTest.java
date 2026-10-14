package com.akr.films.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.akr.films.domain.Identifier;
import com.akr.films.domain.Film;
import com.akr.films.domain.FilmDetail;
import com.akr.films.domain.FilmSummary;
import com.akr.films.exception.ApiExceptionHandler;
import com.akr.films.exception.FilmNotFoundException;
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

    private FilmDetail newHope(String synopsis, String version) {
        return new FilmDetail("1", "A New Hope", "Episode IV - A New Hope", LocalDate.of(1977, 5, 25), synopsis,
                null, "George Lucas", Map.of("openingCrawl", "crawl"), "swapi", version);
    }

    @Test
    void shouldListAllFilms() {
        when(service.findAll()).thenReturn(List.of(
                new FilmSummary("1", "Episode IV - A New Hope"),
                new FilmSummary("2", "Episode V - The Empire Strikes Back")));

        client.get().uri("/films")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo("1")
                .jsonPath("$[0].displayTitle").isEqualTo("Episode IV - A New Hope")
                .jsonPath("$[1].displayTitle").isEqualTo("Episode V - The Empire Strikes Back");
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
    void shouldReturnFilmDetailById() {
        when(service.findById(new Identifier("1"))).thenReturn(newHope(null, "1"));

        client.get().uri("/films/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.title").isEqualTo("A New Hope")
                .jsonPath("$.displayTitle").isEqualTo("Episode IV - A New Hope")
                .jsonPath("$.releaseDate").isEqualTo("1977-05-25")
                .jsonPath("$.director").isEqualTo("George Lucas")
                .jsonPath("$.extras.openingCrawl").isEqualTo("crawl")
                .jsonPath("$.source").isEqualTo("swapi")
                .jsonPath("$.version").isEqualTo("1");
    }

    @Test
    void shouldReturnNotFoundProblemDetailWhenFilmDoesNotExist() {
        when(service.findById(new Identifier("x"))).thenThrow(new FilmNotFoundException("x"));

        client.get().uri("/films/x")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.detail").isEqualTo("Film not found: x");
    }

    @Test
    void shouldUpdateOnlyTheInformedFields() {
        when(service.update(any(Film.class))).thenReturn(newHope("new synopsis", "2"));

        client.patch().uri("/films/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"synopsis":"new synopsis","runtimeMinutes":121}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.synopsis").isEqualTo("new synopsis")
                .jsonPath("$.version").isEqualTo("2");

        ArgumentCaptor<Film> sent = ArgumentCaptor.forClass(Film.class);
        verify(service).update(sent.capture());
        assertEquals(new Identifier("1"), sent.getValue().getId());
        assertEquals("new synopsis", sent.getValue().getSynopsis());
        assertEquals(121, sent.getValue().getRuntimeMinutes());
        assertNull(sent.getValue().getTitle());
        assertNull(sent.getValue().getReleaseDate());
        assertNull(sent.getValue().getDirector());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingFilmThatIsNotLoaded() {
        when(service.update(any(Film.class))).thenThrow(new FilmNotFoundException("x"));

        client.patch().uri("/films/x")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"synopsis":"synopsis"}
                        """)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Film not found: x");
    }

}
