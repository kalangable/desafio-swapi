package com.akr.films.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akr.films.domain.FilmSummary;
import com.akr.films.domain.Franchise;
import com.akr.films.domain.PageResponse;
import com.akr.films.service.FranchiseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/franchises")
@Slf4j
@RequiredArgsConstructor
public class FranchiseController implements FranchiseApi {

    private final FranchiseService service;
    private final PaginationProperties pagination;

    @Override
    @GetMapping
    public List<Franchise> getAll() {
        log.atDebug().addKeyValue("event", "franchises_list").log("GET /franchises");
        return service.findAll();
    }

    @Override
    @GetMapping("/{slug}/films")
    public PageResponse<FilmSummary> getFilms(@PathVariable String slug,
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        log.atDebug().addKeyValue("event", "franchise_films_list").addKeyValue("slug", slug)
                .addKeyValue("page", page).addKeyValue("size", size)
                .log("GET /franchises/{}/films page={} size={}", slug, page, size);
        return service.findFilms(slug, pagination.resolve(page, size));
    }

}
