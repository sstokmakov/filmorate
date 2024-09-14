package ru.tokmakov.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.filmorate.model.Film;
import ru.filmorate.model.Genre;
import ru.filmorate.model.MPARating;
import ru.filmorate.service.film.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    //========================/GET/==============================//

    @GetMapping("/films/{id}")
    public Film get(@PathVariable long id) {
        log.info("==> GET /films/" + id);
        Film film = filmService.getById(id);
        log.info("<== GET /users/" + id + "  Фильм: " + id);
        return film;
    }

    @GetMapping("/films/director/{directorId}")
    public Collection<Film> getDirectorFilmsSorted(@PathVariable long directorId, @RequestParam String sortBy) {
        log.info("==> GET /films/director/" + directorId + "  sortBy: " + sortBy);
        Collection<Film> sortedFilms = filmService.getDirectorFilmsSorted(directorId, sortBy);
        log.info("<== GET /films/director/" + directorId + "  sortBy: " + sortBy + " Размер возвращенного " +
                "отсортированного списка фильмов режисера: " + sortedFilms.size());
        return sortedFilms;
    }

    @GetMapping("/films")
    public Collection<Film> getAll() {
        log.info("==> GET /films ");
        Collection<Film> allFilms = filmService.getAll();
        log.info("<== GET /films Список всех сохраненных фильмов размером: "
                + allFilms.size() + " возвращен");
        return allFilms;
    }

    @GetMapping("/films/popular")
    public Collection<Film> getMostLikedFilms(
            @RequestParam(name = "count", required = false) Long count,
            @RequestParam(name = "genreId", required = false) Integer genreId,
            @RequestParam(name = "year", required = false) Integer year) {
        log.info("==> GET /films/popular?count=" + count + "&" + "genreId=" + genreId + "&" + "year=" + year);
        Collection<Film> mostLikedFilms = filmService.getMostLikedFilms(count, genreId, year);
        log.info("<== GET /films/popular?count="
                + "Самые популярные фильмы в количестве: " + mostLikedFilms.size());
        return mostLikedFilms;
    }

    @GetMapping("/genres")
    public Collection<Genre> getAllGenres() {
        log.info("==> GET /genres");
        Collection<Genre> allGenres = filmService.getAllGenres();
        log.info("<== GET /genres"
                + "Список жанров в количестве: " + allGenres.size());
        return allGenres;
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable int id) {
        log.info("==> GET /genres/" + id);
        Genre genre = filmService.getGenreById(id);
        log.info("<== GET /genres/" + id + "  Жанр фильма: " + genre.getName());
        return genre;
    }

    @GetMapping("/mpa")
    public Collection<MPARating> getAllMpaRating() {
        log.info("==> GET /mpa");
        Collection<MPARating> mpaRating = filmService.getAllMPARatings();
        log.info("<== GET /mpa"
                + "Список рейтингов MPA в количестве: " + mpaRating.size());
        return mpaRating;
    }

    @GetMapping("/mpa/{id}")
    public MPARating getMpaRatingById(@PathVariable int id) {
        log.info("==> GET /mpa/" + id);
        MPARating mpaRating = filmService.getMPARatingById(id);
        log.info("<== GET /mpa/" + id
                + " Рейтинг c id = " + id + " : " + mpaRating.getName());
        return mpaRating;
    }

    @GetMapping("/films/common")
    public List<Film> commonFilms(@RequestParam long userId, @RequestParam long friendId) {
        return filmService.commonFilms(userId, friendId);
    }

    @GetMapping("/films/search")
    public Collection<Film> searchFilms(
            @RequestParam(required = false) String query,
            @RequestParam(name = "by", required = false) List<String> criteria) {
        return filmService.searchFilms(query, criteria);
    }

    //========================/POST/==============================//

    @PostMapping("/films")
    public Film save(@RequestBody Film film) {
        log.info("==> POST /films " + film);
        Film newFilm = filmService.save(film);
        log.info("<== POST /films" + newFilm);
        return newFilm;
    }

    //=========================/PUT/==============================//

    @PutMapping("/films")
    public Film update(@RequestBody Film film) {
        log.info("==> PUT /films " + film);
        Film updatedFilm = filmService.update(film);
        log.info("<== PUT /films" + updatedFilm);
        return updatedFilm;
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("==> PUT /films/" + id + "/like/" + userId);
        filmService.addLike(id, userId);
        log.info("<== PUT /films/" + id + "/like/" + userId + "  Лайк фильму " + filmService.getById(id).getName()
                + " от пользователя с ID=" + userId + " поставлен");
    }

    //========================/DELETE/==============================//

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.info("==> DELETE /films/" + id + "/like/" + userId);
        filmService.deleteLike(id, userId);
        log.info("<== DELETE /films/" + id + "/like/" + userId + "  Лайк фильму " + filmService.getById(id)
                + " от пользователя с ID=" + userId + " удален");
    }

    @DeleteMapping("/films/{filmId}")
    public void deleteFilm(@PathVariable long filmId) {
        log.info("==> DELETE /films/" + filmId);
        filmService.deleteFilm(filmId);
        log.info("<== DELETE /films/" + filmId + " Фильм " + filmId + " удален");
    }
}






