package com.akr.films.domain;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Film {

    private Identifier id;
    private String title;
    private String label;
    private LocalDate releaseDate;
    private String synopsis;
    private Integer runtimeMinutes;
    private String director;
    @Builder.Default
    private Map<String, String> extras = new LinkedHashMap<>();
    private String source;
    private String externalId;
    @Builder.Default
    private Version version = new Version();

    public String getDisplayTitle() {
        return label == null ? title : label + " - " + title;
    }

}
