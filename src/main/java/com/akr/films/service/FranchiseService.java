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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FranchiseService {

    private final FranchiseFindable finder;
    private final FilmMapper mapper;

    public List<Franchise> findAll() {
        log.atDebug().addKeyValue("event", "franchises_find_all").log("Finding franchises");
        return finder.findAllFranchises();
    }

    public PageResponse<FilmSummary> findFilms(String slug, PageQuery query) {
        log.atDebug().addKeyValue("event", "franchise_films_find").addKeyValue("slug", slug)
                .addKeyValue("page", query.page()).addKeyValue("size", query.size())
                .log("Finding films of franchise {}, page {} size {}", slug, query.page(), query.size());
        finder.findFranchiseBySlug(slug).orElseThrow(() -> {
            log.atWarn().addKeyValue("event", "franchise_not_found").addKeyValue("slug", slug)
                    .log("Franchise {} not found", slug);
            return new FranchiseNotFoundException(slug);
        });
        return finder.findByFranchise(slug, query).map(mapper::toSummary);
    }

}
