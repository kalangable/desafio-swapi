package com.akr.films.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class PageResponseTest {

    @Test
    void shouldRoundTheTotalOfPagesUp() {
        PageResponse<String> page = PageResponse.of(List.of("a", "b"), new PageQuery(0, 2), 5);

        assertEquals(new PageInfo(2, 0, 5, 3), page.page());
    }

    @Test
    void shouldHaveNoPagesWhenThereAreNoElements() {
        assertEquals(0, PageResponse.of(List.<String>of(), new PageQuery(0, 2), 0).page().totalPages());
    }

    @Test
    void shouldMapTheContentKeepingThePageInfo() {
        PageResponse<Integer> page = PageResponse.of(List.of("a", "bb"), new PageQuery(1, 2), 4).map(String::length);

        assertEquals(List.of(1, 2), page.content());
        assertEquals(new PageInfo(2, 1, 4, 2), page.page());
    }

}
