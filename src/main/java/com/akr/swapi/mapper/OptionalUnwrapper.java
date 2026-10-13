package com.akr.swapi.mapper;

import java.util.Optional;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OptionalUnwrapper {

    default <T> T unwrap(Optional<T> value) {
        return value != null && value.isPresent() ? value.get() : null;
    }

}
