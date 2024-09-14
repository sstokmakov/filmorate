package ru.tokmakov.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.filmorate.exception.NotFoundException;
import ru.filmorate.exception.ValidationException;
import ru.filmorate.model.*;
import ru.filmorate.repository.feed.FeedRepository;
import ru.filmorate.repository.film.FilmRepository;
import ru.filmorate.repository.genre.GenreRepository;
import ru.filmorate.repository.like.LikeRepository;
import ru.filmorate.repository.mpa.MPARatingRepository;
import ru.filmorate.service.director.DirectorService;
import ru.filmorate.service.user.UserService;
import ru.filmorate.service.validation.ValidationService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BaseFilmService implements FilmService {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;
    private final ValidationService validationService;
    private final MPARatingRepository mpaRatingRepository;
    private final DirectorService directorService;
    private final FeedRepository feedRepository;

    @Override
    public Film getById(long filmId) {
        Film film = filmRepository.getById(filmId).orElseThrow(
                () -> new NotFoundException("Фильм с id=" + filmId + " не найден"));
        film.setGenres(filmRepository.getGenres(filmId));
        film.setDirectors(directorService.getDirectors(filmId));
        return film;
    }

    @Override
    public Film save(Film film) {
        validationService.validateNewData(film);
        try {
            mpaRatingRepository.getMPARatingById(film.getMpa().getId());
        } catch (NotFoundException e) {
            throw new ValidationException("Рейтинг MPA с данным id - не найден");
        }
        Film filmSaved = filmRepository.save(film);
        filmSaved.setGenres(filmRepository.getGenres(film.getId()));
        filmSaved.setDirectors(directorService.getDirectors(film.getId()));
        return filmSaved;
    }

    @Override
    public Film update(Film film) {
        validationService.validateNewData(film);
        Film filmUpdated = filmRepository.update(film);
        filmUpdated.setGenres(filmRepository.getGenres(film.getId()));
        return filmUpdated;
    }

    @Override
    public void addLike(long filmId, long userId) {
        filmRepository.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + filmId + " не найден"));
        userService.getById(userId);
        likeRepository.add(userId, filmId);

        Feed feed = new Feed();
        feed.setEventType(EventType.LIKE);
        feed.setOperation(EventOperation.ADD);
        feed.setUserId(userId);
        feed.setEntityId(filmId);
        feedRepository.add(feed);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        filmRepository.getById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + filmId + " не найден"));
        userService.getById(userId);
        likeRepository.delete(userId, filmId);

        Feed feed = new Feed();
        feed.setEventType(EventType.LIKE);
        feed.setOperation(EventOperation.REMOVE);
        feed.setUserId(userId);
        feed.setEntityId(filmId);
        feedRepository.add(feed);
    }

    @Override
    public Collection<Film> getAll() {
        return filmRepository.getAll();
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return genreRepository.getAll();
    }

    @Override
    public List<Genre> getAllFilmGenres(long filmId) {
        return genreRepository.getAllFilmGenres(filmId);
    }

    @Override
    public Genre getGenreById(Integer genreId) {
        return genreRepository.getGenreById(genreId);
    }

    @Override
    public void deleteFilm(long filmId) {
        filmRepository.deleteById(filmId);
    }

    @Override
    public Collection<Film> getDirectorFilmsSorted(long directorId, String sortBy) {
        directorService.getById(directorId);
        return filmRepository.getSortedFilmsByDirector(directorId, sortBy);
    }

    @Override
    public Collection<MPARating> getAllMPARatings() {
        return mpaRatingRepository.getAllMPARatings();
    }

    @Override
    public MPARating getMPARatingById(int mpaId) {
        return mpaRatingRepository.getMPARatingById(mpaId);
    }

    @Override
    public Collection<Film> getMostLikedFilms(Long limit, Integer genreId, Integer year) {
        return filmRepository.getTopPopular(limit, genreId, year);
    }

    @Override
    public List<Film> commonFilms(long userId, long friendId) {
        Set<Long> userFilms = new HashSet<>(userService.findAllFilmLikes(userId));
        Set<Long> friendFilms = new HashSet<>(userService.findAllFilmLikes(friendId));

        return new ArrayList<>(userFilms.stream()
                .filter(friendFilms::contains)
                .map(filmRepository::getById)
                .map(Optional::orElseThrow)
                .sorted(Collections.reverseOrder(Comparator.comparing(o -> likeRepository.findAllFilmLikes(o.getId()).size())))
                .toList());
    }

    @Override
    public Collection<Film> searchFilms(String query, List<String> criteria) {
        return filmRepository.searchFilms(query, criteria);
    }
}
