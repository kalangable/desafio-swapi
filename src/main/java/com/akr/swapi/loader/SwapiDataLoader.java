package com.akr.swapi.loader;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.akr.swapi.client.SwapiClient;
import com.akr.swapi.domain.Film;
import com.akr.swapi.repository.SwapiFilmCreation;
import com.akr.swapi.service.SwapiImportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j 
public class SwapiDataLoader implements ApplicationRunner {

    private final SwapiImportService importService;
    private final SwapiClient swapiClient;
    private final SwapiFilmCreation repository;

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
