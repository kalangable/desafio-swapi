package com.akr.films.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.akr.films.domain.Film;
import com.akr.films.domain.FilmDetail;
import com.akr.films.domain.FilmSummary;
import com.akr.films.domain.Identifier;


@Mapper(componentModel = "spring")
public interface FilmMapper {

    FilmSummary toSummary(Film film);

    List<FilmSummary> toSummary(List<Film> films);

    @Mapping(target = "version", source = "version.version")
    FilmDetail toDetail(Film film);

    default String map(Identifier id) {
        return id == null ? null : id.value();
    }

}
