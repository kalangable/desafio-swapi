package com.akr.films.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.akr.films.domain.Episode;
import com.akr.films.domain.Film;
import com.akr.films.domain.FilmDetail;
import com.akr.films.domain.FilmSummary;
import com.akr.films.domain.Version;

class FilmMapperTest {

    private final FilmMapper mapper = new FilmMapperImpl();

    private final Film film = new Film("A New Hope", new Episode(4), LocalDate.of(1977, 5, 25), "crawl", new Version(3));

    @Test
    void shouldMapSummaryUsingOnlyTitleAndEpisode() {
        assertEquals(new FilmSummary("A New Hope", "IV"), mapper.toSummary(film));
    }

    @Test
    void shouldMapAllFilmsToSummaries() {
        Film empire = new Film("The Empire Strikes Back", new Episode(5), LocalDate.of(1980, 5, 17), "crawl");

        List<FilmSummary> summaries = mapper.toSummary(List.of(film, empire));

        assertEquals(List.of(new FilmSummary("A New Hope", "IV"), new FilmSummary("The Empire Strikes Back", "V")), summaries);
    }

    @Test
    void shouldMapDetailWithEpisodeAsRomanAndVersionAsText() {
        FilmDetail expected = new FilmDetail("A New Hope", "IV", LocalDate.of(1977, 5, 25), "crawl", "3");
        assertEquals(expected, mapper.toDetail(film));
    }

}
