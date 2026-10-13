package com.akr.films.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.akr.films.controller.FilmRequestUpdate;
import com.akr.films.domain.Episode;
import com.akr.films.domain.Film;
import com.akr.films.util.EpisodeParser;

@Mapper(componentModel = "spring", uses = OptionalUnwrapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FilmRequestMapper {

    @Mapping(target = "version", ignore = true)
    Film toFilm(String episode, FilmRequestUpdate request);

    default Episode toEpisode(String episode) {
        return EpisodeParser.parse(episode);
    }

}
