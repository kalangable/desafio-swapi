package com.akr.films.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akr.films.domain.FilmDetail;
import com.akr.films.domain.FilmSummary;
import com.akr.films.domain.Identifier;
import com.akr.films.domain.PageResponse;
import com.akr.films.mapper.FilmRequestMapper;
import com.akr.films.service.FilmService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController implements FilmApi {

    private final FilmService service;
    private final FilmRequestMapper requestMapper;
    private final PaginationProperties pagination;

    @Override
    @GetMapping
    public PageResponse<FilmSummary> getAll(@RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return service.findAll(pagination.resolve(page, size));
    }

    @Override
    @GetMapping("/{id}")
    public FilmDetail getById(@PathVariable String id) {
        return service.findById(new Identifier(id));
    }

    @Override
    @PatchMapping("/{id}")
    public FilmDetail update(@PathVariable String id, @RequestBody FilmRequestUpdate request) {
        var filmMapped = requestMapper.toFilm(id, request);
        return service.update(filmMapped);
    }
}
