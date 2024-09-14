package ru.tokmakov.filmorate.repository.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.filmorate.exception.NotFoundException;
import ru.filmorate.exception.ValidationException;
import ru.filmorate.model.Director;
import ru.filmorate.repository.director.mapper.DirectorRowMapper;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcDirectorRepository implements DirectorRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Optional<Director> getById(long directorId) {
        String sql = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = :directorId";
        try {
            return Optional.ofNullable(
                    jdbc.queryForObject(sql, Map.of("directorId", directorId), new DirectorRowMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    @Override
    public Director save(Director director) {
        GeneratedKeyHolder keyHolderDirector = new GeneratedKeyHolder();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", director.getName());

        if (getById(director.getId()).isEmpty()) {
            jdbc.update("INSERT INTO DIRECTORS (NAME) VALUES (:name)", params, keyHolderDirector);
            director.setId(keyHolderDirector.getKeyAs(Long.class));
            return director;
        } else {
            throw new ValidationException("Режиссер с данным id = " + director.getId() + " уже есть");
        }
    }

    @Override
    public Director update(Director director) {
        if (getById(director.getId()).isPresent()) {
            String sqlUpdate = "UPDATE DIRECTORS SET NAME = :name WHERE DIRECTOR_ID = :directorId";
            jdbc.update(sqlUpdate, Map.of("directorId", director.getId(), "name", director.getName()));
            return getById(director.getId()).orElseThrow();
        } else {
            throw new NotFoundException("Режиссер с данным id " + director.getId() + " не найден");
        }
    }

    @Override
    public void deleteById(long directorId) {

        String queryDelFromDir = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = :directorId";
        jdbc.update(queryDelFromDir, Map.of("directorId", directorId));

    }


    @Override
    public Collection<Director> getAll() {
        return jdbc.query("SELECT * FROM DIRECTORS", new DirectorRowMapper());
    }

    @Override
    public LinkedHashSet<Director> getDirectors(long filmId) {
        String query = "SELECT * " +
                "FROM DIRECTORS AS d " +
                "JOIN FILM_DIRECTOR AS fd ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "WHERE fd.FILM_ID = :filmId";
        Collection<Director> directors = jdbc.query(query, Map.of("filmId", filmId), new DirectorRowMapper());
        return new LinkedHashSet<>(directors);
    }

}