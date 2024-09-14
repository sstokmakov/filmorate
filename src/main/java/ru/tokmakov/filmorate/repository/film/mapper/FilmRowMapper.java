package ru.tokmakov.filmorate.repository.film.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.filmorate.model.Film;
import ru.filmorate.model.MPARating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(LocalDate.parse(rs.getString("release_date")));
        film.setDuration(rs.getLong("duration_in_min"));
        film.setMpa(new MPARating(
                rs.getInt("mpa_rating_id"), rs.getString("MPA_RATING.name")));
        return film;
    }
}
