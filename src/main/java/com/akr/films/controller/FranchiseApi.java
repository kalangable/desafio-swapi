package com.akr.films.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;

import com.akr.films.domain.FilmSummary;
import com.akr.films.domain.Franchise;
import com.akr.films.domain.PageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Franchises", description = "Groups of films, such as a saga or a series")
public interface FranchiseApi {

    @Operation(summary = "List all franchises",
            description = "Returns the slug and the name of every franchise that has at least one loaded film. "
                    + "The list is empty when no film has been loaded.")
    @ApiResponse(responseCode = "200", description = "Franchises found (possibly none)",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = Franchise.class))))
    List<Franchise> getAll();

    @Operation(summary = "List the films of a franchise",
            description = "Returns one page with the id and the display title of the films of the franchise "
                    + "identified by the slug. A film never appears in more than one page.")
    @ApiResponse(responseCode = "200", description = "Page of films of the franchise",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PageResponse.class)))
    @ApiResponse(responseCode = "400", description = "Page is negative, or size is below 1 or above the maximum",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "No loaded film belongs to a franchise with this slug",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
    PageResponse<FilmSummary> getFilms(
            @Parameter(description = "Franchise slug", examples = {
                    @ExampleObject(name = "star wars", value = "star-wars") })
            String slug,
            @Parameter(description = "Page number, starting at 0 (see pagination.default-page)") Integer page,
            @Parameter(description = "Films per page (see pagination.default-size and pagination.max-size)") Integer size);

}
