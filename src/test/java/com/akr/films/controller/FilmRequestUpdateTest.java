package com.akr.films.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class FilmRequestUpdateTest {

    @Test
    void shouldListOnlyThePresentFields() {
        FilmRequestUpdate request = new FilmRequestUpdate(Optional.of("Title"), Optional.empty(),
                Optional.of("Synopsis"), Optional.of(120), Optional.empty());

        assertEquals(List.of("title", "synopsis", "runtimeMinutes"), request.presentFields());
    }

    @Test
    void shouldListAllFieldsWhenAllArePresent() {
        FilmRequestUpdate request = new FilmRequestUpdate(Optional.of("Title"),
                Optional.of(LocalDate.of(1977, 5, 25)), Optional.of("Synopsis"), Optional.of(120),
                Optional.of("Director"));

        assertEquals(List.of("title", "releaseDate", "synopsis", "runtimeMinutes", "director"),
                request.presentFields());
    }

    @Test
    void shouldListNothingWhenNoFieldIsPresent() {
        FilmRequestUpdate request = new FilmRequestUpdate(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty());

        assertTrue(request.presentFields().isEmpty());
    }

}
