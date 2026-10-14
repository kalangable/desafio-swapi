package com.akr.films.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.akr.films.controller.FilmRequestUpdate;
import com.akr.films.domain.Film;
import com.akr.films.domain.Identifier;

@Mapper(componentModel = "spring", uses = OptionalUnwrapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FilmRequestMapper {

    @Mapping(target = "franchise", ignore = true)
    @Mapping(target = "label", ignore = true)
    @Mapping(target = "extras", ignore = true)
    @Mapping(target = "source", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "version", ignore = true)
    Film toFilm(String id, FilmRequestUpdate request);

    default Identifier toIdentifier(String id) {
        return new Identifier(id);
    }

}
