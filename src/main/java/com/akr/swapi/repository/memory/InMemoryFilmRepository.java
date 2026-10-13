package com.akr.swapi.repository.memory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.akr.swapi.domain.Film;
import com.akr.swapi.repository.FilmCreation;
import com.akr.swapi.repository.FilmFindable;
import com.akr.swapi.repository.FilmUpdatable;

@Repository
public class InMemoryFilmRepository implements FilmCreation, FilmFindable, FilmUpdatable {

    private final ConcurrentHashMap<Integer, Film> map = new ConcurrentHashMap<>();

    @Override
    public Film insert(Film data) {
        map.put(data.getEpisode().toInt(), data);
        return data;
    }

    @Override
    public List<Film> findAll() {
        return List.copyOf(map.values());
    }

    @Override
    public Optional<Film> findById(Integer id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public Film update(Film data) {
        map.put(data.getEpisode().toInt(), data);
        return data;
    }

}
