package com.akr.films.service;

import org.springframework.stereotype.Service;

import com.akr.films.domain.Film;
import com.akr.films.domain.FilmDetail;
import com.akr.films.domain.FilmSummary;
import com.akr.films.domain.Identifier;
import com.akr.films.domain.PageQuery;
import com.akr.films.domain.PageResponse;
import com.akr.films.exception.FilmNotFoundException;
import com.akr.films.mapper.FilmMapper;
import com.akr.films.mapper.FilmUpdateMapper;
import com.akr.films.repository.FilmFindable;
import com.akr.films.repository.FilmUpdatable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmFindable finder;
    private final FilmUpdatable updater;
    private final FilmMapper mapper;
    private final FilmUpdateMapper updateMapper;

    public PageResponse<FilmSummary> findAll(PageQuery query) {
        log.atDebug().addKeyValue("event", "films_find_all").addKeyValue("page", query.page())
                .addKeyValue("size", query.size()).log("Finding films, page {} size {}", query.page(), query.size());
        return finder.findAll(query).map(mapper::toSummary);
    }

    public FilmDetail findById(Identifier id) {
        log.atDebug().addKeyValue("event", "film_find").addKeyValue("filmId", id.value()).log("Finding film {}", id);
        return finder.findById(id)
                .map(mapper::toDetail)
                .orElseThrow(() -> {
                    log.atWarn().addKeyValue("event", "film_not_found").addKeyValue("filmId", id.value())
                            .log("Film {} not found", id);
                    return new FilmNotFoundException(id.value());
                });
    }

    public FilmDetail update(Film changes) {
        Film current = finder.findById(changes.getId())
                .orElseThrow(() -> {
                    log.atWarn().addKeyValue("event", "film_not_found").addKeyValue("filmId", changes.getId().value())
                            .log("Film {} not found for update", changes.getId());
                    return new FilmNotFoundException(changes.getId().value());
                });
        
        Film before = current.toBuilder().build();

        updateMapper.apply(changes, current);
        if (current.equals(before)) {
            log.atDebug().addKeyValue("event", "film_unchanged").addKeyValue("filmId", current.getId().value())
                    .addKeyValue("version", current.getVersion().version())
                    .log("Film {} unchanged, version kept at {}", current.getId(), current.getVersion().version());
            return mapper.toDetail(current);
        }

        current.setVersion(current.getVersion().increment());
        Film saved = updater.update(current);
        log.atInfo().addKeyValue("event", "film_updated").addKeyValue("filmId", saved.getId().value())
                .addKeyValue("version", saved.getVersion().version())
                .log("Film {} updated to version {}", saved.getId(), saved.getVersion().version());
        return mapper.toDetail(saved);
    }
}
