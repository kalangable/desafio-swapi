package com.akr.films.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import com.akr.films.domain.PageQuery;
import com.akr.films.domain.PageResponse;
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
        client = RestTestClient.bindToController(new FilmController(service, new FilmRequestMapperImpl(new OptionalUnwrapperImpl()),
                new PaginationProperties(0, 2, 100)))
                .configureServer(server -> server.setControllerAdvice(new ApiExceptionHandler()))
                .build();
    }

    private FilmDetail newHope(String synopsis, String version) {
        return new FilmDetail("1", "A New Hope", "Episode IV - A New Hope", "Star Wars", LocalDate.of(1977, 5, 25), synopsis,
                null, "George Lucas", Map.of("openingCrawl", "crawl"), "swapi", version);
    }

    private PageResponse<FilmSummary> page(PageQuery query, long total, FilmSummary... films) {
        return PageResponse.of(List.of(films), query, total);
    }

    @Test
    void shouldListTheDefaultPageWhenNoParameterIsInformed() {
        PageQuery query = new PageQuery(0, 2);
        when(service.findAll(query)).thenReturn(page(query, 3,
                new FilmSummary("1", "Episode IV - A New Hope", "Star Wars"),
                new FilmSummary("2", "Episode V - The Empire Strikes Back", "Star Wars")));

        client.get().uri("/films")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo(2)
                .jsonPath("$.content[0].id").isEqualTo("1")
                .jsonPath("$.content[0].displayTitle").isEqualTo("Episode IV - A New Hope")
                .jsonPath("$.content[0].franchise").isEqualTo("Star Wars")
                .jsonPath("$.content[1].displayTitle").isEqualTo("Episode V - The Empire Strikes Back")
                .jsonPath("$.page.size").isEqualTo(2)
                .jsonPath("$.page.number").isEqualTo(0)
                .jsonPath("$.page.totalElements").isEqualTo(3)
                .jsonPath("$.page.totalPages").isEqualTo(2);
    }

    @Test
    void shouldPassTheInformedPageAndSizeToTheService() {
        PageQuery query = new PageQuery(2, 5);
        when(service.findAll(query)).thenReturn(page(query, 11, new FilmSummary("11", "Heat", null)));

        client.get().uri("/films?page=2&size=5")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.page.number").isEqualTo(2)
                .jsonPath("$.page.size").isEqualTo(5);
        verify(service).findAll(query);
    }

    @Test
    void shouldReturnEmptyContentWhenThereAreNoFilms() {
        PageQuery query = new PageQuery(0, 2);
        when(service.findAll(query)).thenReturn(page(query, 0));

        client.get().uri("/films")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo(0)
                .jsonPath("$.page.totalElements").isEqualTo(0);
    }

    @Test
    void shouldReturnBadRequestWhenThePaginationIsInvalid() {
        for (String query : List.of("page=-1", "size=0", "size=101")) {
            client.get().uri("/films?" + query)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(400);
        }
        verifyNoInteractions(service);
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
                .jsonPath("$.franchise").isEqualTo("Star Wars")
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
