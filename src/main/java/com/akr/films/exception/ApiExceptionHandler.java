package com.akr.films.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(InvalidPaginationException.class)
    ProblemDetail handleInvalidPagination(InvalidPaginationException e) {
        log.atWarn().addKeyValue("event", "request_rejected").addKeyValue("status", 400)
                .addKeyValue("reason", e.getMessage()).log("Bad request: {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(FranchiseNotFoundException.class)
    ProblemDetail handleFranchiseNotFound(FranchiseNotFoundException e) {
        log.atWarn().addKeyValue("event", "request_rejected").addKeyValue("status", 404)
                .addKeyValue("reason", e.getMessage()).log("Not found: {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(FilmNotFoundException.class)
    ProblemDetail handleFilmNotFound(FilmNotFoundException e) {
        log.atWarn().addKeyValue("event", "request_rejected").addKeyValue("status", 404)
                .addKeyValue("reason", e.getMessage()).log("Not found: {}", e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

}
