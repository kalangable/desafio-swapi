package com.akr.swapi.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class EpisodeTest {

    @Test
    void shouldShowRomanNumeral() {
        assertEquals("I", new Episode(1).toString());
        assertEquals("IV", new Episode(4).toString());
        assertEquals("IX", new Episode(9).toString());
        assertEquals("VIII", new Episode(8).toString());
        assertEquals("X", new Episode(10).toString());
    }

    @Test
    void shouldReturnInt() {
        assertEquals(4, new Episode(4).toInt());
    }

    @Test
    void shouldRejectOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> new Episode(0));
        assertThrows(IllegalArgumentException.class, () -> new Episode(11));
    }

}
