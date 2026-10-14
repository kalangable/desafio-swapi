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
        log.atInfo().addKeyValue("event", "import_started").addKeyValue("sources", sources.size())
                .log("Importing films from {} source(s)", sources.size());
        for (FilmSource source : sources) {
            String name = source.getClass().getSimpleName();
            try {
                log.atInfo().addKeyValue("event", "source_import_started").addKeyValue("sourceClass", name)
                        .log("Start process to import data from {}", name);
                load(name, source.fetch());
            } catch (RuntimeException e) {
                log.atError().addKeyValue("event", "source_import_failed").addKeyValue("sourceClass", name).setCause(e)
                        .log("Failed to import data from {}", name);
            }
        }
        log.atInfo().addKeyValue("event", "import_finished").log("Import finished");
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
        int imported = films.size() - problems.size();
        log.atInfo().addKeyValue("event", "source_import_summary").addKeyValue("sourceClass", name)
                .addKeyValue("imported", imported).addKeyValue("total", films.size())
                .log("Imported {} of {} films from {}", imported, films.size(), name);
        problems.forEach(problem -> log.atWarn().addKeyValue("event", "film_skipped").addKeyValue("sourceClass", name)
                .addKeyValue("reason", problem).log("{}: film skipped, {}", name, problem));
    }

    private void insert(Film film) {
        if (film.getId() == null) {
            throw new InvalidFilmException("Film without id: " + film.getTitle());
        }
        if (finder.findById(film.getId()).isPresent()) {
            throw new FilmAlreadyExistsException(film.getId().value());
        }
        creator.insert(film);
        log.atDebug().addKeyValue("event", "film_inserted").addKeyValue("filmId", film.getId().value())
                .log("Film {} inserted", film.getId());
    }

}
