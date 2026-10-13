package com.akr.films.domain;

public record Version(int version) {

    public Version() {
        this(1);
    }

    public Version increment() {
        return new Version(version + 1);
    }

}
