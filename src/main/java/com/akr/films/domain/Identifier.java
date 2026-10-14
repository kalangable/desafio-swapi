package com.akr.films.domain;

public record Identifier(String value) {

    public Identifier {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Identifier can't be blank");
        }
    }

    @Override
    public String toString() {
        return value;
    }

}
