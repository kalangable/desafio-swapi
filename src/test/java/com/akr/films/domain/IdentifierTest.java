package com.akr.films.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class IdentifierTest {

    @Test
    void shouldExposeTheValueAsText() {
        Identifier identifier = new Identifier("star-wars-1");

        assertEquals("star-wars-1", identifier.value());
        assertEquals("star-wars-1", identifier.toString());
    }

    @Test
    void shouldBeEqualWhenTheValueIsEqual() {
        assertEquals(new Identifier("star-wars-1"), new Identifier("star-wars-1"));
    }

    @Test
    void shouldRejectNullOrBlankValue() {
        assertThrows(IllegalArgumentException.class, () -> new Identifier(null));
        assertThrows(IllegalArgumentException.class, () -> new Identifier(" "));
    }

}
