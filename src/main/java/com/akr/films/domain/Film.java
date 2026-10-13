package com.akr.films.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Film {

    private String title;
    private Episode episode;
    private LocalDate releaseDate;
    private String description;
    @Builder.Default
    private Version version = new Version();

    public Film(String title, Episode episode, LocalDate releaseDate, String description) {
        this(title, episode, releaseDate, description, new Version());
    }

}
