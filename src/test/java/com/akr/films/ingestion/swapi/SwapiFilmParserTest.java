package com.akr.films.ingestion.swapi;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.DateTimeException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.akr.films.domain.Film;
import com.akr.films.domain.Franchise;
import com.akr.films.domain.Identifier;

class SwapiFilmParserTest {

    private SwapiFilmParser parser;

    @BeforeEach
    void setUp() {
        parser = new SwapiFilmParser();
    }

    @Test
    void shouldProcessFilmsJsonWithoutErrors() throws IOException {
        String json = readFilmsJson();

        assertDoesNotThrow(() -> parser.parse(json));
    }

    @Test
    void shouldFillTheFilmWithTheDataOfTheSource() throws IOException {
        List<Film> films = parser.parse(readFilmsJson());

        Film newHope = films.stream().filter(f -> f.getTitle().equals("A New Hope")).findFirst().orElseThrow();
        assertEquals(new Franchise("star-wars", "Star Wars"), newHope.getFranchise());
        assertEquals("Episode IV", newHope.getLabel());
        assertEquals("Episode IV - A New Hope", newHope.getDisplayTitle());
        assertEquals("George Lucas", newHope.getDirector());
        assertEquals("swapi", newHope.getSource());
        assertEquals("https://swapi.py4e.com/api/films/1/", newHope.getExternalId());
        assertEquals("Gary Kurtz, Rick McCallum", newHope.getExtras().get("producer"));
        assertTrue(newHope.getExtras().get("openingCrawl").startsWith("It is a period of civil war."));
        assertNull(newHope.getSynopsis());
        assertNull(newHope.getRuntimeMinutes());
        assertEquals(new Identifier("star-wars-4"), newHope.getId());
    }

    @Test
    void shouldFailWhenReleaseDateIsInvalid() {
        String json = "{\"results\":[{\"title\":\"x\",\"episode_id\":1,\"release_date\":\"invalid\",\"opening_crawl\":\"\"}]}";

        assertThrows(DateTimeException.class, () -> parser.parse(json));
    }

    @Test
    void shouldFailWhenEpisodeIsOutOfRange() {
        String json = "{\"results\":[{\"title\":\"x\",\"episode_id\":11,\"release_date\":\"2000-01-01\",\"opening_crawl\":\"\"}]}";

        assertThrows(IllegalArgumentException.class, () -> parser.parse(json));
    }

    private String readFilmsJson() throws IOException {
        try (var in = getClass().getResourceAsStream("/films.json")) {
            return new String(in.readAllBytes());
        }
    }

}
