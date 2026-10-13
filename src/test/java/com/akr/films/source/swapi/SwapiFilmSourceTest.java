package com.akr.films.source.swapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import com.akr.films.domain.Episode;
import com.akr.films.domain.Film;
import com.akr.films.source.HttpFetcher;

import reactor.core.publisher.Mono;

class SwapiFilmSourceTest {

    @Test
    void shouldFetchResourceAndParseFilms() {
        HttpFetcher fetch = mock(HttpFetcher.class);
        SwapiFilmParser parser = mock(SwapiFilmParser.class);
        List<Film> films = List.of(new Film("A New Hope", new Episode(4), LocalDate.of(1977, 5, 25), "crawl"));
        when(fetch.get(any(WebClient.class), eq("/films"))).thenReturn(Mono.just("{json}"));
        when(parser.parse("{json}")).thenReturn(films);

        SwapiFilmSource source = new SwapiFilmSource(new SwapiProperties("http://localhost", "/films"), fetch, parser);

        assertThat(source.fetch()).isEqualTo(films);
    }
}
