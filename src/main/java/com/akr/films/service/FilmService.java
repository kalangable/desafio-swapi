package com.akr.films.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.akr.films.domain.Episode;
import com.akr.films.domain.Film;
import com.akr.films.domain.FilmDetail;
import com.akr.films.domain.FilmSummary;
import com.akr.films.exception.FilmNotFoundException;
import com.akr.films.mapper.FilmMapper;
import com.akr.films.mapper.FilmUpdateMapper;
import com.akr.films.repository.FilmFindable;
import com.akr.films.repository.FilmUpdatable;
import com.akr.films.util.EpisodeParser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmFindable finder;
    private final FilmUpdatable updater;
    private final FilmMapper mapper;
    private final FilmUpdateMapper updateMapper;

    public List<FilmSummary> findAll() {
        return mapper.toSummary(finder.findAll());
    }

    public FilmDetail findByEpisode(String value) {
        Episode episode = EpisodeParser.parse(value);
        return finder.findById(episode.toInt())
                .map(mapper::toDetail)
                .orElseThrow(() -> new FilmNotFoundException(episode.toString()));
    }

    public FilmDetail update(Film changes) {
        Film current = finder.findById(changes.getEpisode().toInt())
                .orElseThrow(() -> new FilmNotFoundException(changes.getEpisode().toString()));
        
        Film before = current.toBuilder().build();

        updateMapper.apply(changes, current);
        if (current.equals(before)) {
            return mapper.toDetail(current);
        }

        current.setVersion(current.getVersion().increment());
        return mapper.toDetail(updater.update(current));
    }
}
