package com.akr.swapi.loader;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.akr.swapi.client.SwapiClient;
import com.akr.swapi.domain.Film;
import com.akr.swapi.repository.SwapiCreation;
import com.akr.swapi.service.SwapiImportService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SwapiDataLoader implements ApplicationRunner {

    private final SwapiImportService importService;
    private final SwapiClient swapiClient;
    private final SwapiCreation<Film> repository;

    @Override
    public void run(ApplicationArguments args) {
        String json = importService.getData().block();
        List<Film> films = swapiClient.process(json);
        for (Film film : films) {
            repository.insert(film);
        }
    }

}
