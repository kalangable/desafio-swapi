package com.akr.films.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.akr.films.domain.Episode;
import com.akr.films.domain.Film;
import com.akr.films.domain.Version;

class FilmUpdateMapperTest {

    private final FilmUpdateMapper mapper = new FilmUpdateMapperImpl();

    private final Film current = new Film("A New Hope", new Episode(4), LocalDate.of(1977, 5, 25), "crawl", new Version(1));

    @Test
    void shouldReplaceOnlyTheFieldsThatAreNotNull() {
        mapper.apply(new Film(null, null, null, "new crawl", null), current);

        assertEquals("A New Hope", current.getTitle());
        assertEquals(LocalDate.of(1977, 5, 25), current.getReleaseDate());
        assertEquals("new crawl", current.getDescription());
    }

    @Test
    void shouldReplaceAllEditableFieldsWhenNoneIsNull() {
        mapper.apply(new Film("Episode IV", null, LocalDate.of(1978, 1, 1), "new crawl", null), current);

        assertEquals("Episode IV", current.getTitle());
        assertEquals(LocalDate.of(1978, 1, 1), current.getReleaseDate());
        assertEquals("new crawl", current.getDescription());
    }

    @Test
    void shouldChangeNothingWhenAllFieldsAreNull() {
        mapper.apply(new Film(), current);

        assertEquals("A New Hope", current.getTitle());
        assertEquals(LocalDate.of(1977, 5, 25), current.getReleaseDate());
        assertEquals("crawl", current.getDescription());
    }

    @Test
    void shouldNeverChangeEpisodeOrVersion() {
        Film changes = new Film("Other", new Episode(5), LocalDate.of(1980, 5, 17), "other", new Version(9));

        mapper.apply(changes, current);

        assertEquals(4, current.getEpisode().toInt());
        assertEquals(new Version(1), current.getVersion());
    }

}
