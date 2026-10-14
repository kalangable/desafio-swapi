package com.akr.films.ingestion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;


import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

class HttpFetcherTest {

    private final HttpClientProperties properties = new HttpClientProperties(
            Duration.ofMillis(100), 3, Duration.ofMillis(10));

    private static final String URL = "http://localhost/films";

    private final AtomicInteger calls = new AtomicInteger();

    private WebClient webClient;

    private HttpFetcher service;

    @BeforeEach
    void setUp() {
        webClient = mock(WebClient.class, RETURNS_DEEP_STUBS);
        service = new HttpFetcher(properties);
    }

    @Test
    void shouldReturnBodyWhenFirstAttemptSucceeds() {
        mockApiResponse(Mono.defer(() -> {
            calls.incrementAndGet();
            return Mono.just("ok");
        }));

        assertThat(service.get(webClient, URL).block()).isEqualTo("ok");
        assertThat(calls).hasValue(1);
    }

    @Test
    void shouldRetryAfterErrorAndSucceed() {
        mockApiResponse(Mono.defer(() -> calls.incrementAndGet() < 3
                ? Mono.error(new RuntimeException("Server error"))
                : Mono.just("ok")));

        assertThat(service.get(webClient, URL).block()).isEqualTo("ok");
        assertThat(calls).hasValue(3);
    }

    @Test
    void shouldFailAfterRetriesAreExhausted() {
        mockApiResponse(Mono.defer(() -> {
            calls.incrementAndGet();
            return Mono.error(new RuntimeException("Server error"));
        }));

        assertThatThrownBy(() -> service.get(webClient, URL).block())
                .matches(Exceptions::isRetryExhausted)
                .hasRootCauseMessage("Server error");
        assertThat(calls).hasValue(4); // 1 attempt + 3 retries
    }

    @Test
    void shouldRetryAfterTimeoutAndSucceed() {
        mockApiResponse(Mono.defer(() -> calls.incrementAndGet() == 1
                ? Mono.never()
                : Mono.just("ok")));

        assertThat(service.get(webClient, URL).block()).isEqualTo("ok");
        assertThat(calls).hasValue(2);
    }

    @Test
    void shouldFailWithTimeoutAfterRetriesAreExhausted() {
        mockApiResponse(Mono.defer(() -> {
            calls.incrementAndGet();
            return Mono.never();
        }));

        assertThatThrownBy(() -> service.get(webClient, URL).block())
                .matches(Exceptions::isRetryExhausted)
                .hasRootCauseInstanceOf(TimeoutException.class);
        assertThat(calls).hasValue(4);
    }

    private void mockApiResponse(Mono<String> response) {
        when(webClient.get().uri(URL).retrieve()
                .onStatus(any(), any())
                .onStatus(any(), any())
                .bodyToMono(String.class))
                .thenReturn(response);
    }
}
