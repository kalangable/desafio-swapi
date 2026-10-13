package com.akr.films.client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.time.DateTimeException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SwapiClientTest {

    private SwapiClient client;

    @BeforeEach
    void setUp() {
        client = new SwapiClient();
    }

    @Test
    void shouldProcessFilmsJsonWithoutErrors() throws IOException {
        String json = readFilmsJson();

        assertDoesNotThrow(() -> client.process(json));
    }

    @Test
    void shouldFailWhenReleaseDateIsInvalid() {
        String json = "{\"results\":[{\"title\":\"x\",\"episode_id\":1,\"release_date\":\"invalid\",\"opening_crawl\":\"\"}]}";

        assertThrows(DateTimeException.class, () -> client.process(json));
    }

    @Test
    void shouldFailWhenEpisodeIsOutOfRange() {
        String json = "{\"results\":[{\"title\":\"x\",\"episode_id\":11,\"release_date\":\"2000-01-01\",\"opening_crawl\":\"\"}]}";

        assertThrows(IllegalArgumentException.class, () -> client.process(json));
    }

    private String readFilmsJson() throws IOException {
        try (var in = getClass().getResourceAsStream("/films.json")) {
            return new String(in.readAllBytes());
        }
    }

}
