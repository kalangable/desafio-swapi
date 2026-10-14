package com.akr.films.repository.memory;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.akr.films.domain.Film;
import com.akr.films.domain.Identifier;
import com.akr.films.domain.PageQuery;
import com.akr.films.domain.PageResponse;
import com.akr.films.repository.FilmCreation;
import com.akr.films.repository.FilmFindable;
import com.akr.films.repository.FilmUpdatable;

@Repository
public class InMemoryFilmRepository implements FilmCreation, FilmFindable, FilmUpdatable {

    private final ConcurrentHashMap<Identifier, Film> map = new ConcurrentHashMap<>();

    @Override
    public Film insert(Film data) {
        map.put(data.getId(), data);
        return data;
    }

    @Override
    public PageResponse<Film> findAll(PageQuery query) {
        List<Film> films = map.values().stream()
                .sorted(Comparator.comparing(film -> film.getId().value()))
                .toList();
        int from = (int) Math.min(query.offset(), films.size());
        int to = (int) Math.min(query.offset() + query.size(), films.size());
        return PageResponse.of(films.subList(from, to), query, films.size());
    }

    @Override
    public Optional<Film> findById(Identifier id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public Film update(Film data) {
        map.put(data.getId(), data);
        return data;
    }

}
