package ru.tokmakov.filmorate.repository.like;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JdbcLikeRepository implements LikeRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public void add(long userId, long filmId) {
        String sql = "merge into likes (film_id, user_Id) values (:filmId, :userId)";
        jdbc.update(sql, Map.of("filmId", filmId, "userId", userId));
    }

    @Override
    public void delete(long userId, long filmId) {
        String sql = "DELETE FROM LIKES WHERE film_id = :film_id AND user_id = :user_id";
        jdbc.update(sql, Map.of("film_id", filmId, "user_id", userId));
    }

    @Override
    public Set<Long> findAllFilmLikes(long filmId) {
        String sql = "select user_id from LIKES where film_id = :film_id";
        MapSqlParameterSource params = new MapSqlParameterSource("film_id", filmId);
        return new HashSet<>(jdbc.queryForList(sql, params, Long.class));
    }
}
