package com.akr.films.exception;

public class FilmNotFoundException extends RuntimeException {

    public FilmNotFoundException(String episode) {
        super("Film not found for episode: " + episode);
    }

}
