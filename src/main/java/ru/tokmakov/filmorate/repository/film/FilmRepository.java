package ru.tokmakov.filmorate.repository.film;

import ru.filmorate.model.Film;
import ru.filmorate.model.Genre;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

public interface FilmRepository {

    Film save(Film film);

    void deleteById(long filmId);

    Film update(Film film);

    Collection<Film> getAll();

    Optional<Film> getById(long filmId);

    Collection<Film> getTopPopular(Long count, Integer genreId, Integer year);

    Collection<Film> getSortedFilmsByDirector(long directorId, String sortBy);

    LinkedHashSet<Genre> getGenres(long filmId);

    Collection<Film> searchFilms(String query, List<String> criteria);
}
