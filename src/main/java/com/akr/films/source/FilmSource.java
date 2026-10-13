package com.akr.films.source;

import java.util.List;

import com.akr.films.domain.Film;

public interface FilmSource {

    List<Film> fetch();

}
