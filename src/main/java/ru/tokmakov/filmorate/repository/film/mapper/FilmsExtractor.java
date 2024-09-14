package ru.tokmakov.filmorate.repository.film.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.filmorate.model.Film;
import ru.filmorate.model.MPARating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class FilmsExtractor implements ResultSetExtractor<List<Film>> {
    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Film> films = new LinkedHashMap<>();
        while (rs.next()) {
            Long filmId = rs.getLong("FILMS.film_id");
            if (films.get(filmId) == null) {
                Film film = new Film();
                film.setId(filmId);
                film.setName(rs.getString("FILMS.name"));
                film.setDescription(rs.getString("FILMS.description"));
                LocalDate releaseLocalDate = Objects.requireNonNull(
                        rs.getDate("FILMS.release_date").toLocalDate());
                film.setReleaseDate(releaseLocalDate);
                film.setDuration(rs.getLong("FILMS.duration_in_min"));
                film.setMpa(new MPARating(
                        rs.getInt("mpa_rating_id"), rs.getString("MPA_RATING.name")));
                films.put(filmId, film);
            }

        }
        return new LinkedList<>(films.values());
    }
}

