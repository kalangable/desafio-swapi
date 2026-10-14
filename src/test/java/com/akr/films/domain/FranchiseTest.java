package com.akr.films.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class FranchiseTest {

    @Test
    void shouldKeepSlugAndName() {
        Franchise franchise = new Franchise("fast-and-furious", "Fast & Furious");

        assertEquals("fast-and-furious", franchise.slug());
        assertEquals("Fast & Furious", franchise.name());
    }

    @Test
    void shouldAcceptASingleWordSlug() {
        assertEquals("matrix", new Franchise("matrix", "Matrix").slug());
    }

    @Test
    void shouldRejectInvalidSlugs() {
        for (String slug : new String[] { null, "", "Star Wars", "star_wars", "Star-Wars", "-star", "star-", "star--wars" }) {
            assertThrows(IllegalArgumentException.class, () -> new Franchise(slug, "Name"), "slug: " + slug);
        }
    }

    @Test
    void shouldRejectBlankName() {
        assertThrows(IllegalArgumentException.class, () -> new Franchise("star-wars", null));
        assertThrows(IllegalArgumentException.class, () -> new Franchise("star-wars", " "));
    }

}
