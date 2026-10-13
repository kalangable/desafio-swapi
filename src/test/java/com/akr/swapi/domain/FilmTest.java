package com.akr.swapi.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class FilmTest {

    @Test
    void shouldStartAtVersionOneWhenVersionIsNotInformed() {
        Film film = new Film("A New Hope", new Episode(4), LocalDate.of(1977, 5, 25), "crawl");
        assertEquals(new Version(1), film.version());
    }

    @Test
    void shouldKeepInformedVersion() {
        Film film = new Film("A New Hope", new Episode(4), LocalDate.of(1977, 5, 25), "crawl", new Version(3));
        assertEquals(3, film.version().version());
    }

}
