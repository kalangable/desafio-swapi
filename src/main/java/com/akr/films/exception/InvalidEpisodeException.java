package com.akr.films.exception;

public class InvalidEpisodeException extends RuntimeException {

    public InvalidEpisodeException(String value) {
        super("Invalid episode: " + value);
    }

}
