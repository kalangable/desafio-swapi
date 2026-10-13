package com.akr.films.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.akr.films.domain.Film;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FilmUpdateMapper {

    @Mapping(target = "episode", ignore = true)
    @Mapping(target = "version", ignore = true)
    void apply(Film changes, @MappingTarget Film current);

}
