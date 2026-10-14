package com.akr.films.controller;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record FilmRequestUpdate(
        Optional<String> title,
        Optional<LocalDate> releaseDate,
        Optional<String> synopsis,
        Optional<Integer> runtimeMinutes,
        Optional<String> director) {

    public List<String> presentFields() {
        Map<String, Optional<?>> fields = new LinkedHashMap<>();
        fields.put("title", title);
        fields.put("releaseDate", releaseDate);
        fields.put("synopsis", synopsis);
        fields.put("runtimeMinutes", runtimeMinutes);
        fields.put("director", director);
        return fields.entrySet().stream()
                .filter(field -> field.getValue() != null && field.getValue().isPresent())
                .map(Map.Entry::getKey)
                .toList();
    }

}
