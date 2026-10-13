package com.akr.swapi.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class FilmTest {

    @Test
    void shouldStartAtVersionOneWhenVersionIsNotInformed() {
        Film film = new Film("A New Hope", new Episode(4), LocalDate.of(1977, 5, 25), "crawl");
        assertEquals(new Version(1), film.getVersion());
    }

    @Test
    void shouldKeepInformedVersion() {
        Film film = new Film("A New Hope", new Episode(4), LocalDate.of(1977, 5, 25), "crawl", new Version(3));
        assertEquals(3, film.getVersion().version());
    }

    @Test
    void shouldStartAtVersionOneWhenCreatedWithoutParameters() {
        assertEquals(new Version(1), new Film().getVersion());
    }

    @Test
    void shouldCopyAllFields() {
        Film film = new Film("A New Hope", new Episode(4), LocalDate.of(1977, 5, 25), "crawl", new Version(3));
        Film copy = film.toBuilder().build();

        assertEquals(film, copy);
        copy.setTitle("Other");
        assertEquals("A New Hope", film.getTitle());
    }

}
