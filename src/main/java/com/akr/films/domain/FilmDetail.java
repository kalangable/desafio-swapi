package com.akr.films.domain;

import java.time.LocalDate;
import java.util.Map;

public record FilmDetail(
        String id,
        String title,
        String displayTitle,
        String franchise,
        LocalDate releaseDate,
        String synopsis,
        Integer runtimeMinutes,
        String director,
        Map<String, String> extras,
        String source,
        String version) {
}
