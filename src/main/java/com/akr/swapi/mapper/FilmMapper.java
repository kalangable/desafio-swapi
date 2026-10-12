package com.akr.swapi.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.akr.swapi.domain.Episode;
import com.akr.swapi.domain.Film;
import com.akr.swapi.domain.FilmSummary;


@Mapper(componentModel = "spring")
public interface FilmMapper {

    FilmSummary toSummary(Film film);

    List<FilmSummary> toSummary(List<Film> films);

    default String map(Episode episode) {
        return episode == null ? null : episode.toString();
    }

}
