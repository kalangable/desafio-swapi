package com.akr.swapi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akr.swapi.domain.FilmDetail;
import com.akr.swapi.domain.FilmSummary;
import com.akr.swapi.mapper.FilmRequestMapper;
import com.akr.swapi.service.FilmService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController implements FilmApi {

    private final FilmService service;
    private final FilmRequestMapper requestMapper;

    @Override
    @GetMapping
    public List<FilmSummary> getAll() {
        return service.findAll();
    }

    @Override
    @GetMapping("/{episode}")
    public FilmDetail getByEpisode(@PathVariable String episode) {
        return service.findByEpisode(episode);
    }

    @Override
    @PatchMapping("/{episode}")
    public FilmDetail update(@PathVariable String episode, @RequestBody FilmRequestUpdate request) {
        var filmMapped = requestMapper.toFilm(episode, request);
        return service.update(filmMapped);
    }
}
