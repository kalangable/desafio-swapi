package com.akr.swapi.repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.akr.swapi.domain.Film;

@Repository
public class InMemoryFilmRepository implements SwapiFilmCreation, SwapiFilmFindable {

    private final ConcurrentHashMap<Integer, Film> map = new ConcurrentHashMap<>();

    @Override
    public Film insert(Film data) {
        map.put(data.episode().toInt(), data);
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

}
