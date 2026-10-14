package com.akr.films.repository.memory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.akr.films.domain.Film;
import com.akr.films.repository.FilmCreation;
import com.akr.films.repository.FilmFindable;
import com.akr.films.repository.FilmUpdatable;

@Repository
public class InMemoryFilmRepository implements FilmCreation, FilmFindable, FilmUpdatable {

    private final ConcurrentHashMap<String, Film> map = new ConcurrentHashMap<>();

    @Override
    public Film insert(Film data) {
        data.setId(UUID.randomUUID().toString());
        map.put(data.getId(), data);
        return data;
    }

    @Override
    public List<Film> findAll() {
        return List.copyOf(map.values());
    }

    @Override
    public Optional<Film> findById(String id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public Film update(Film data) {
        map.put(data.getId(), data);
        return data;
    }

}
