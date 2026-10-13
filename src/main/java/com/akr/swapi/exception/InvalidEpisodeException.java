package com.akr.swapi.exception;

public class InvalidEpisodeException extends RuntimeException {

    public InvalidEpisodeException(String value) {
        super("Invalid episode: " + value);
    }

}
