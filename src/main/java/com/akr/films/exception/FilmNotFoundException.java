package com.akr.films.exception;

public class FilmNotFoundException extends RuntimeException {

    public FilmNotFoundException(String id) {
        super("Film not found: " + id);
    }

}
