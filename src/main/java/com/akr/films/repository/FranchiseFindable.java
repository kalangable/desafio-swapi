package com.akr.films.repository;

import java.util.List;
import java.util.Optional;

import com.akr.films.domain.Film;
import com.akr.films.domain.Franchise;
import com.akr.films.domain.PageQuery;
import com.akr.films.domain.PageResponse;

public interface FranchiseFindable {

    List<Franchise> findAllFranchises();

    Optional<Franchise> findFranchiseBySlug(String slug);

    PageResponse<Film> findByFranchise(String slug, PageQuery query);

}
