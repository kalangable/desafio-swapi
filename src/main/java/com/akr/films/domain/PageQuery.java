package com.akr.films.domain;

public record PageQuery(int page, int size) {

    public long offset() {
        return (long) page * size;
    }

}
