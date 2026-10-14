package com.akr.films.controller;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.akr.films.domain.PageQuery;
import com.akr.films.exception.InvalidPaginationException;

@ConfigurationProperties(prefix = "pagination")
public record PaginationProperties(
        int defaultPage,
        int defaultSize,
        int maxSize) {

    public PageQuery resolve(Integer page, Integer size) {
        int resolvedPage = page == null ? defaultPage : page;
        int resolvedSize = size == null ? defaultSize : size;
        if (resolvedPage < 0) {
            throw new InvalidPaginationException("Page must be zero or greater: " + resolvedPage);
        }
        if (resolvedSize < 1 || resolvedSize > maxSize) {
            throw new InvalidPaginationException("Size must be between 1 and " + maxSize + ": " + resolvedSize);
        }
        return new PageQuery(resolvedPage, resolvedSize);
    }

}
