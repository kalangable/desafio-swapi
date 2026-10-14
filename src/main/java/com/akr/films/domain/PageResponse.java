package com.akr.films.domain;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(List<T> content, PageInfo page) {

    public static <T> PageResponse<T> of(List<T> content, PageQuery query, long totalElements) {
        int totalPages = (int) ((totalElements + query.size() - 1) / query.size());
        return new PageResponse<>(content, new PageInfo(query.size(), query.page(), totalElements, totalPages));
    }

    public <R> PageResponse<R> map(Function<T, R> mapper) {
        return new PageResponse<>(content.stream().map(mapper).toList(), page);
    }

}
