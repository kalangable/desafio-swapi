package com.akr.swapi.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.akr.swapi.controller.FilmRequestUpdate;
import com.akr.swapi.domain.Episode;
import com.akr.swapi.domain.Film;
import com.akr.swapi.util.EpisodeParser;

@Mapper(componentModel = "spring", uses = OptionalUnwrapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FilmRequestMapper {

    @Mapping(target = "version", ignore = true)
    Film toFilm(String episode, FilmRequestUpdate request);

    default Episode toEpisode(String episode) {
        return EpisodeParser.parse(episode);
    }

}
