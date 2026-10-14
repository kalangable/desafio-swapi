package com.akr.films.ingestion;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.akr.films.domain.Film;
import com.akr.films.repository.FilmCreation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j 
public class FilmDataLoader implements ApplicationRunner {

    private final List<FilmSource> sources;
    private final FilmCreation repository;

    @Override
    public void run(ApplicationArguments args) {
        for (FilmSource source : sources) {
            try {
                log.info("Start process to import data from {}", source.getClass().getSimpleName());
                for (Film film : source.fetch()) {
                    repository.insert(film);
                }
            } catch (RuntimeException e) {
                log.error("Failed to import data from {}", source.getClass().getSimpleName(), e);
            }
        }
    }

}
