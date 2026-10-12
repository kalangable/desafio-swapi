package com.akr.swapi.repository;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.akr.swapi.domain.Film;

@Repository
public class InMemoryFilmRepository implements SwapiCreation<Film> {

    private final ConcurrentHashMap<Integer, Film> map = new ConcurrentHashMap<>();

    @Override
    public Film insert(Film data) {
        map.put(data.episode().toInt(), data);
        return data;
    }

}
