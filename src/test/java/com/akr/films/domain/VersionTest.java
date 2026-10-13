package com.akr.films.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class VersionTest {

    @Test
    void shouldStartAtOneWhenCreatedWithoutParameter() {
        assertEquals(1, new Version().version());
    }

    @Test
    void shouldIncrementByOne() {
        assertEquals(2, new Version().increment().version());
    }

    @Test
    void shouldNotChangeOriginalInstance() {
        Version original = new Version(5);
        Version updated =  original.increment();
        assertEquals(6, updated.version());
        assertEquals(5, original.version());
    }

}
