package ru.tokmakov.filmorate.repository.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.filmorate.exception.NotFoundException;
import ru.filmorate.model.Genre;
import ru.filmorate.repository.genre.mapper.GenreRowMapper;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public List<Genre> getAllFilmGenres(Long filmId) {
        String sql = "SELECT g.genre_name from film_genre AS fg " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id where film_id = :filmId";
        return jdbc.queryForList(sql, Map.of("filmId", filmId), Genre.class);
    }

    @Override
    public Collection<Genre> getAll() {
        String sql = "select * from genres";
        List<Genre> genresList = jdbc.query(sql, new GenreRowMapper());
        HashMap<Integer, Genre> genresMap = new HashMap<>();
        for (Genre genre : genresList) {
            genresMap.put(genre.getId(), genre);
        }
        return new LinkedHashSet<>(genresMap.values());
    }

    @Override
    public Genre getGenreById(Integer genreId) {
        try {
            String sql = "SELECT * from genres where genre_Id = :genreId";
            return jdbc.queryForObject(sql, Map.of("genreId", genreId), new GenreRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Не найдет genreID = " + genreId);
        }
    }
}