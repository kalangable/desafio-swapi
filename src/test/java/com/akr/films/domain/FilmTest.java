package com.akr.films.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class FilmTest {

    private Film.FilmBuilder newHope() {
        return Film.builder().title("A New Hope").releaseDate(LocalDate.of(1977, 5, 25));
    }

    @Test
    void shouldStartAtVersionOneWhenVersionIsNotInformed() {
        assertEquals(new Version(1), newHope().build().getVersion());
    }

    @Test
    void shouldKeepInformedVersion() {
        assertEquals(3, newHope().version(new Version(3)).build().getVersion().version());
    }

    @Test
    void shouldStartAtVersionOneWhenCreatedWithoutParameters() {
        assertEquals(new Version(1), new Film().getVersion());
    }

    @Test
    void shouldStartWithoutExtras() {
        assertTrue(newHope().build().getExtras().isEmpty());
        assertTrue(new Film().getExtras().isEmpty());
    }

    @Test
    void shouldCopyAllFields() {
        Film film = newHope().id("1").label("Episode IV").version(new Version(3)).build();
        Film copy = film.toBuilder().build();

        assertEquals(film, copy);
        copy.setTitle("Other");
        assertEquals("A New Hope", film.getTitle());
    }

    @Test
    void shouldUseLabelAndTitleAsDisplayTitle() {
        assertEquals("Episode IV - A New Hope", newHope().label("Episode IV").build().getDisplayTitle());
    }

    @Test
    void shouldUseOnlyTitleAsDisplayTitleWhenThereIsNoLabel() {
        assertEquals("A New Hope", newHope().build().getDisplayTitle());
    }

}
