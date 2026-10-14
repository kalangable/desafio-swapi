package com.akr.films.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;

import com.akr.films.domain.FilmDetail;
import com.akr.films.domain.FilmSummary;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Films", description = "Films loaded from the configured sources")
public interface FilmApi {

    @Operation(summary = "List all films",
            description = "Returns the id and the display title (for example, \"Episode IV - A New Hope\") "
                    + "of every loaded film. The list is empty when no film has been loaded.")
    @ApiResponse(responseCode = "200", description = "Films found (possibly none)",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = FilmSummary.class))))
    List<FilmSummary> getAll();

    @Operation(summary = "Get the details of a film",
            description = "Finds a film by id. Synopsis, runtime and director are optional and may be null "
                    + "when the source does not provide them; extras holds data specific to the source "
                    + "(for example, the opening crawl of Star Wars films).")
    @ApiResponse(responseCode = "200", description = "Film found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FilmDetail.class)))
    @ApiResponse(responseCode = "404", description = "No film was loaded with this id",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
    FilmDetail getById(
            @Parameter(description = "Film id", examples = {
                    @ExampleObject(name = "uuid", value = "3f2b8c1e-5d4a-4b7e-9a60-1c2d3e4f5a6b") })
            String id);

    @Operation(summary = "Update part of a film",
            description = "Changes only the fields informed in the body (title, releaseDate, synopsis, "
                    + "runtimeMinutes and director); fields left out keep their current value. "
                    + "The version is incremented only when something actually changes.")
    @ApiResponse(responseCode = "200", description = "Film updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FilmDetail.class)))
    @ApiResponse(responseCode = "404", description = "No film was loaded with this id",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
    FilmDetail update(
            @Parameter(description = "Film id", examples = {
                    @ExampleObject(name = "uuid", value = "3f2b8c1e-5d4a-4b7e-9a60-1c2d3e4f5a6b") })
            String id,
            FilmRequestUpdate request);

}
