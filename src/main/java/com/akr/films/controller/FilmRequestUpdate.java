package com.akr.films.controller;

import java.time.LocalDate;
import java.util.Optional;

public record FilmRequestUpdate(
        Optional<String> title,
        Optional<LocalDate> releaseDate,
        Optional<String> synopsis,
        Optional<Integer> runtimeMinutes,
        Optional<String> director) {
}
