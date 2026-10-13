package com.akr.swapi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akr.swapi.domain.FilmDetail;
import com.akr.swapi.domain.FilmSummary;
import com.akr.swapi.service.SwapiFilmService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class SwapiController {

    private final SwapiFilmService service;

    @GetMapping
    public List<FilmSummary> getAll() {
        return service.findAll();
    }

    @GetMapping("/{episode}")
    public FilmDetail getByEpisode(@PathVariable String episode) {
        return service.findByEpisode(episode);
    }
}
