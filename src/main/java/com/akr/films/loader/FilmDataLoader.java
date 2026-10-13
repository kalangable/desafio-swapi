package com.akr.films.loader;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.akr.films.client.SwapiClient;
import com.akr.films.domain.Film;
import com.akr.films.repository.FilmCreation;
import com.akr.films.service.SwapiImportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j 
public class FilmDataLoader implements ApplicationRunner {

    private final SwapiImportService importService;
    private final SwapiClient swapiClient;
    private final FilmCreation repository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Start process to Import data from SWApi");
        String json = importService.getData().block();
        List<Film> films = swapiClient.process(json);
        for (Film film : films) {
            repository.insert(film);
        }
    }

}
