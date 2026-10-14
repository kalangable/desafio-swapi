package com.akr.films.repository;

import java.util.Optional;

import com.akr.films.domain.Film;
import com.akr.films.domain.Identifier;
import com.akr.films.domain.PageQuery;
import com.akr.films.domain.PageResponse;

public interface FilmFindable {

    PageResponse<Film> findAll(PageQuery query);

    Optional<Film> findById(Identifier id);

}
