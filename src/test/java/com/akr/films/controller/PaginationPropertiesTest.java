package com.akr.films.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.akr.films.domain.PageQuery;
import com.akr.films.exception.InvalidPaginationException;

class PaginationPropertiesTest {

    private final PaginationProperties properties = new PaginationProperties(0, 2, 100);

    @Test
    void shouldUseTheDefaultsWhenNothingIsInformed() {
        assertEquals(new PageQuery(0, 2), properties.resolve(null, null));
    }

    @Test
    void shouldUseTheInformedValues() {
        assertEquals(new PageQuery(3, 10), properties.resolve(3, 10));
    }

    @Test
    void shouldAcceptTheMaximumSize() {
        assertEquals(new PageQuery(0, 100), properties.resolve(0, 100));
    }

    @Test
    void shouldRejectNegativePage() {
        assertThrows(InvalidPaginationException.class, () -> properties.resolve(-1, 2));
    }

    @Test
    void shouldRejectSizeBelowOneOrAboveTheMaximum() {
        assertThrows(InvalidPaginationException.class, () -> properties.resolve(0, 0));
        assertThrows(InvalidPaginationException.class, () -> properties.resolve(0, 101));
    }

}
