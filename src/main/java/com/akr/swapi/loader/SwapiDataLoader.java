package com.akr.swapi.loader;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.akr.swapi.client.SwapiClient;
import com.akr.swapi.service.SwapiImportService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SwapiDataLoader implements ApplicationRunner {

    private final SwapiImportService importService;
    private final SwapiClient swapiClient;

    @Override
    public void run(ApplicationArguments args) {
        String json = importService.getData().block();
        swapiClient.process(json);
    }

}
