package com.akr.films.domain;

import java.util.regex.Pattern;

public record Franchise(String slug, String name) {

    private static final Pattern SLUG = Pattern.compile("[a-z0-9]+(-[a-z0-9]+)*");

    public Franchise {
        if (slug == null || !SLUG.matcher(slug).matches()) {
            throw new IllegalArgumentException("Franchise slug must be lowercase words separated by hyphens: " + slug);
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Franchise name can't be blank");
        }
    }

}
