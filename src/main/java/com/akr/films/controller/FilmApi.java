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

@Tag(name = "Films", description = "Star Wars films imported from SWAPI")
public interface FilmApi {

    @Operation(summary = "List all films",
            description = "Returns the title and episode (roman numeral) of every loaded film. "
                    + "The list is empty when no film has been loaded.")
    @ApiResponse(responseCode = "200", description = "Films found (possibly none)",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = FilmSummary.class))))
    List<FilmSummary> getAll();

    @Operation(summary = "Get the details of a film",
            description = "Finds a film by episode, informed either as a number (1 to 10) or as a roman numeral "
                    + "(I to X, case insensitive).")
    @ApiResponse(responseCode = "200", description = "Film found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FilmDetail.class)))
    @ApiResponse(responseCode = "400", description = "Episode is not a number from 1 to 10 nor a roman numeral from I to X",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Valid episode, but no film was loaded for it",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
    FilmDetail getByEpisode(
            @Parameter(description = "Episode as a number or roman numeral", examples = {
                    @ExampleObject(name = "number", value = "4"),
                    @ExampleObject(name = "roman numeral", value = "IV") })
            String episode);

    @Operation(summary = "Update part of a film",
            description = "Changes only the fields informed in the body (title, releaseDate and description); "
                    + "fields left out keep their current value. The film is identified by the episode in the URL, "
                    + "as a number from 1 to 10 or a roman numeral from I to X, and its version is incremented.")
    @ApiResponse(responseCode = "200", description = "Film updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FilmDetail.class)))
    @ApiResponse(responseCode = "400", description = "Episode is not a number from 1 to 10 nor a roman numeral from I to X",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Valid episode, but no film was loaded for it",
            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
    FilmDetail update(
            @Parameter(description = "Episode as a number or roman numeral", examples = {
                    @ExampleObject(name = "number", value = "4"),
                    @ExampleObject(name = "roman numeral", value = "IV") })
            String episode,
            FilmRequestUpdate request);

}
