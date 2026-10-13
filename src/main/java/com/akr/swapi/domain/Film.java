package com.akr.swapi.domain;

import java.time.LocalDate;

public record Film(String title, Episode episode, LocalDate releaseDate, String description, Version version) {

    public Film(String title, Episode episode, LocalDate releaseDate, String description) {
        this(title, episode, releaseDate, description, new Version());
    }

}
