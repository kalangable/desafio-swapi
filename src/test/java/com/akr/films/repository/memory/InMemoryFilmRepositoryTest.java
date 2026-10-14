package com.akr.films.repository.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.akr.films.domain.Film;
import com.akr.films.domain.Franchise;
import com.akr.films.domain.Identifier;
import com.akr.films.domain.PageInfo;
import com.akr.films.domain.PageQuery;
import com.akr.films.domain.PageResponse;

class InMemoryFilmRepositoryTest {

    private final InMemoryFilmRepository repository = new InMemoryFilmRepository();

    private Film.FilmBuilder newHope() {
        return Film.builder()
                .id(new Identifier("star-wars-1"))
                .title("A New Hope")
                .releaseDate(LocalDate.of(1977, 5, 25));
    }

    @Test
    void shouldReturnInsertedFilm() {
        Film film = newHope().build();
        assertEquals(film, repository.insert(film));
    }

    @Test
    void shouldFindInsertedFilmById() {
        Film film = repository.insert(newHope().build());

        assertEquals(Optional.of(film), repository.findById(new Identifier("star-wars-1")));
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {
        assertTrue(repository.findById(new Identifier("missing")).isEmpty());
    }

    private void insertFilms(int count) {
        for (int i = 1; i <= count; i++) {
            repository.insert(Film.builder().id(new Identifier("star-wars-" + i)).title("Film " + i).build());
        }
    }

    private List<String> ids(PageResponse<Film> page) {
        return page.content().stream().map(film -> film.getId().value()).toList();
    }

    @Test
    void shouldReturnTheRequestedPage() {
        insertFilms(5);

        PageResponse<Film> page = repository.findAll(new PageQuery(1, 2));

        assertEquals(2, page.content().size());
        assertEquals(new PageInfo(2, 1, 5, 3), page.page());
    }

    @Test
    void shouldNotRepeatFilmsAcrossPages() {
        insertFilms(5);

        List<String> all = new ArrayList<>();
        for (int page = 0; page < 3; page++) {
            all.addAll(ids(repository.findAll(new PageQuery(page, 2))));
        }

        assertEquals(5, all.size());
        assertEquals(5, new HashSet<>(all).size());
    }

    @Test
    void shouldReturnTheSameFilmsWhenThePageIsRequestedAgain() {
        insertFilms(5);

        assertEquals(ids(repository.findAll(new PageQuery(1, 2))), ids(repository.findAll(new PageQuery(1, 2))));
    }

    @Test
    void shouldReturnTheRemainingFilmsInTheLastPage() {
        insertFilms(5);

        assertEquals(1, repository.findAll(new PageQuery(2, 2)).content().size());
    }

    @Test
    void shouldReturnEmptyContentWhenThePageIsPastTheLastOne() {
        insertFilms(3);

        PageResponse<Film> page = repository.findAll(new PageQuery(5, 2));

        assertTrue(page.content().isEmpty());
        assertEquals(new PageInfo(2, 5, 3, 2), page.page());
    }

    @Test
    void shouldReturnEmptyPageWhenThereAreNoFilms() {
        PageResponse<Film> page = repository.findAll(new PageQuery(0, 2));

        assertTrue(page.content().isEmpty());
        assertEquals(new PageInfo(2, 0, 0, 0), page.page());
    }

    private static final Franchise STAR_WARS = new Franchise("star-wars", "Star Wars");

    private static final Franchise FAST = new Franchise("fast-and-furious", "Fast & Furious");

    private void insertFilm(String id, Franchise franchise) {
        repository.insert(Film.builder().id(new Identifier(id)).franchise(franchise).title(id).build());
    }

    @Test
    void shouldListTheDistinctFranchisesOrderedBySlug() {
        insertFilm("star-wars-1", STAR_WARS);
        insertFilm("fast-and-furious-1", FAST);
        insertFilm("star-wars-2", STAR_WARS);
        insertFilm("no-franchise-1", null);

        assertEquals(List.of(FAST, STAR_WARS), repository.findAllFranchises());
    }

    @Test
    void shouldReturnNoFranchisesWhenThereAreNoFilms() {
        assertTrue(repository.findAllFranchises().isEmpty());
    }

    @Test
    void shouldFindAFranchiseBySlugIgnoringCase() {
        insertFilm("star-wars-1", STAR_WARS);

        assertEquals(Optional.of(STAR_WARS), repository.findFranchiseBySlug("star-wars"));
        assertEquals(Optional.of(STAR_WARS), repository.findFranchiseBySlug("STAR-WARS"));
        assertTrue(repository.findFranchiseBySlug("fast-and-furious").isEmpty());
    }

    @Test
    void shouldPageOnlyTheFilmsOfTheFranchise() {
        insertFilm("star-wars-1", STAR_WARS);
        insertFilm("star-wars-2", STAR_WARS);
        insertFilm("star-wars-3", STAR_WARS);
        insertFilm("fast-and-furious-1", FAST);
        insertFilm("no-franchise-1", null);

        PageResponse<Film> first = repository.findByFranchise("star-wars", new PageQuery(0, 2));
        PageResponse<Film> second = repository.findByFranchise("star-wars", new PageQuery(1, 2));

        assertEquals(List.of("star-wars-1", "star-wars-2"), ids(first));
        assertEquals(List.of("star-wars-3"), ids(second));
        assertEquals(new PageInfo(2, 0, 3, 2), first.page());
    }

    @Test
    void shouldReturnAnEmptyPageForAFranchiseWithoutFilms() {
        insertFilm("star-wars-1", STAR_WARS);

        PageResponse<Film> page = repository.findByFranchise("fast-and-furious", new PageQuery(0, 2));

        assertTrue(page.content().isEmpty());
        assertEquals(0, page.page().totalElements());
    }

    @Test
    void shouldReplaceFilmOnUpdate() {
        Film inserted = repository.insert(newHope().build());
        Film updated = inserted.toBuilder().synopsis("new synopsis").build();

        assertEquals(updated, repository.update(updated));
        assertEquals(Optional.of(updated), repository.findById(inserted.getId()));
    }

}
