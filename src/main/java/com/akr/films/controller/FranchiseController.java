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

@RestController
@RequestMapping("/franchises")
@RequiredArgsConstructor
public class FranchiseController implements FranchiseApi {

    private final FranchiseService service;
    private final PaginationProperties pagination;

    @Override
    @GetMapping
    public List<Franchise> getAll() {
        return service.findAll();
    }

    @Override
    @GetMapping("/{slug}/films")
    public PageResponse<FilmSummary> getFilms(@PathVariable String slug,
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        return service.findFilms(slug, pagination.resolve(page, size));
    }

}
