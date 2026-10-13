package com.akr.swapi.controller;

import java.time.LocalDate;
import java.util.Optional;

public record FilmRequestUpdate(Optional<String> title, Optional<LocalDate> releaseDate, Optional<String> description) {
}
