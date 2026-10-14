package com.akr.films.exception;

public class FilmAlreadyExistsException extends InvalidFilmException {

    public FilmAlreadyExistsException(String id) {
        super("Film id already exists: " + id);
    }

}
