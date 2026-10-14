package com.akr.films.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.akr.films.domain.Franchise;
import com.akr.films.domain.FilmSummary;
import com.akr.films.domain.PageQuery;
import com.akr.films.domain.PageResponse;
import com.akr.films.exception.FranchiseNotFoundException;
import com.akr.films.mapper.FilmMapper;
import com.akr.films.repository.FranchiseFindable;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FranchiseService {

    private final FranchiseFindable finder;
    private final FilmMapper mapper;

    public List<Franchise> findAll() {
        return finder.findAllFranchises();
    }

    public PageResponse<FilmSummary> findFilms(String slug, PageQuery query) {
        finder.findFranchiseBySlug(slug).orElseThrow(() -> new FranchiseNotFoundException(slug));
        return finder.findByFranchise(slug, query).map(mapper::toSummary);
    }

}
