package com.akr.swapi.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.akr.swapi.domain.Episode;
import com.akr.swapi.domain.FilmDetail;
import com.akr.swapi.domain.FilmSummary;
import com.akr.swapi.exception.FilmNotFoundException;
import com.akr.swapi.mapper.FilmMapper;
import com.akr.swapi.repository.SwapiFilmFindable;
import com.akr.swapi.util.EpisodeParser;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class SwapiFilmService {

    private final SwapiFilmFindable repository;
    private final FilmMapper mapper;

    public List<FilmSummary> findAll(){
        return mapper.toSummary(repository.findAll());
    }

    public FilmDetail findByEpisode(String value) {
        Episode episode = EpisodeParser.parse(value);
        return repository.findById(episode.toInt())
                .map(mapper::toDetail)
                .orElseThrow(() -> new FilmNotFoundException(episode.toString()));
    }
}
