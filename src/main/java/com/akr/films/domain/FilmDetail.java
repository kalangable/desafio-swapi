package com.akr.swapi.domain;

import java.time.LocalDate;

public record FilmDetail(String title, String episode, LocalDate releaseDate, String description, String version) {
}
