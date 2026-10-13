package com.akr.swapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.akr.swapi.domain.Film;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FilmUpdateMapper {

    @Mapping(target = "episode", ignore = true)
    @Mapping(target = "version", ignore = true)
    void apply(Film changes, @MappingTarget Film current);

}
