package com.akr.swapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.akr.swapi.domain.FilmSummary;
import com.akr.swapi.mapper.FilmMapper;
import com.akr.swapi.repository.SwapiFilmFindable;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class SwapiFilmService {

    private final SwapiFilmFindable repository;
    private final FilmMapper mapper;

    public List<FilmSummary> findAll(){
        return mapper.toSummary(repository.findAll());
    }
}
