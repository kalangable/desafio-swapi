package com.akr.films.ingestion;

import java.util.List;

import com.akr.films.domain.Film;

public interface FilmSource {

    List<Film> fetch();

}
