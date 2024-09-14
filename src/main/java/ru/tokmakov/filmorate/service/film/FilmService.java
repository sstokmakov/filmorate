package ru.tokmakov.filmorate.service.film;

import ru.filmorate.model.Film;
import ru.filmorate.model.Genre;
import ru.filmorate.model.MPARating;

import java.util.Collection;
import java.util.List;

public interface FilmService {

    Film getById(long filmId);

    Film save(Film film);

    Film update(Film film);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    Collection<Film> getAll();

    Collection<Genre> getAllGenres();

    List<Genre> getAllFilmGenres(long filmId);

    MPARating getMPARatingById(int filmId);

    Collection<MPARating> getAllMPARatings();

    Collection<Film> getMostLikedFilms(Long limit, Integer genreId, Integer year);

    Genre getGenreById(Integer genreId);

    Collection<Film> getDirectorFilmsSorted(long directorId, String sortBy);

    void deleteFilm(long filmId);

    List<Film> commonFilms(long userId, long friendId);

    Collection<Film> searchFilms(String query, List<String> criteria);
}
