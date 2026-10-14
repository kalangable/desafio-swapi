package com.akr.films.ingestion;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.akr.films.domain.Film;
import com.akr.films.exception.FilmAlreadyExistsException;
import com.akr.films.exception.InvalidFilmException;
import com.akr.films.repository.FilmCreation;
import com.akr.films.repository.FilmFindable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j 
public class FilmDataLoader implements ApplicationRunner {

    private final List<FilmSource> sources;
    private final FilmCreation creator;
    private final FilmFindable finder;

    @Override
    public void run(ApplicationArguments args) {
        for (FilmSource source : sources) {
            String name = source.getClass().getSimpleName();
            try {
                log.info("Start process to import data from {}", name);
                load(name, source.fetch());
            } catch (RuntimeException e) {
                log.error("Failed to import data from {}", name, e);
            }
        }
    }

    private void load(String name, List<Film> films) {
        List<String> problems = new ArrayList<>();
        for (Film film : films) {
            try {
                insert(film);
            } catch (InvalidFilmException e) {
                problems.add(e.getMessage());
            }
        }
        log.info("Imported {} of {} films from {}", films.size() - problems.size(), films.size(), name);
        problems.forEach(problem -> log.warn("{}: {}", name, problem));
    }

    private void insert(Film film) {
        if (film.getId() == null) {
            throw new InvalidFilmException("Film without id: " + film.getTitle());
        }
        if (finder.findById(film.getId()).isPresent()) {
            throw new FilmAlreadyExistsException(film.getId().value());
        }
        creator.insert(film);
    }

}
