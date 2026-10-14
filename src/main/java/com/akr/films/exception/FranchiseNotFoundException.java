package com.akr.films.exception;

public class FranchiseNotFoundException extends RuntimeException {

    public FranchiseNotFoundException(String slug) {
        super("Franchise not found: " + slug);
    }

}
