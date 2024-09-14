package ru.tokmakov.filmorate.repository.review;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.filmorate.exception.NotFoundException;
import ru.filmorate.model.Review;
import ru.filmorate.repository.review.mapper.ReviewRowMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcReviewRepository implements ReviewRepository {
    private final NamedParameterJdbcTemplate jdbc;
    private final ReviewRowMapper reviewRowMapper;

    @Override
    public Review create(Review review) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO REVIEWS (content, is_positive, user_id, film_id)" +
                " VALUES (:content, :isPositive, :userId, :filmId)";
        jdbc.update(sql, getParams(review), keyHolder);
        if (keyHolder.getKey() != null) {
            review.setId(keyHolder.getKey().intValue());
        }
        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE REVIEWS SET content=:content, " +
                "is_positive=:isPositive " +
                "WHERE review_id=:id";
        int countRow = jdbc.update(sql, getParams(review));
        if (countRow != 1) {
            throw new NotFoundException("Review not found");
        }
        Optional<Review> updateOptional = getById(review.getId());

        return updateOptional.orElseThrow(() -> new NotFoundException("Review not found"));
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM REVIEWS WHERE review_id=:id";
        jdbc.update(sql, Map.of("id", id));
    }

    @Override
    public Optional<Review> getById(Long id) {
        String sql = "select REVIEWS.REVIEW_ID,REVIEWS.CONTENT,REVIEWS.IS_POSITIVE,REVIEWS.USER_ID,REVIEWS.FILM_ID, " +
                "       COALESCE(sum(RL.SCORE),0) useful " +
                "from REVIEWS " +
                "left join PUBLIC.REVIEWS_LIKES RL on REVIEWS.REVIEW_ID = RL.REVIEW_ID " +
                "where REVIEWS.REVIEW_ID=:id " +
                "group by REVIEWS.REVIEW_ID";
        try {
            Review result = jdbc.queryForObject(sql, Map.of("id", id), reviewRowMapper);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Review> getByFilmLimit(Long filmId, int count) {
        String sql = "select REVIEWS.REVIEW_ID,REVIEWS.CONTENT,REVIEWS.IS_POSITIVE,REVIEWS.USER_ID,REVIEWS.FILM_ID, " +
                "       COALESCE(sum(RL.SCORE),0) useful " +
                "from REVIEWS " +
                "left join PUBLIC.REVIEWS_LIKES RL on REVIEWS.REVIEW_ID = RL.REVIEW_ID " +
                "WHERE film_id = :filmId " +
                "group by REVIEWS.REVIEW_ID " +
                "ORDER BY useful DESC " +
                "LIMIT :count";
        return jdbc.query(sql, Map.of("filmId", filmId, "count", count), reviewRowMapper);
    }

    @Override
    public List<Review> getByFilmLimit(int count) {
        String sql = "select REVIEWS.REVIEW_ID,REVIEWS.CONTENT,REVIEWS.IS_POSITIVE,REVIEWS.USER_ID,REVIEWS.FILM_ID, " +
                "       COALESCE(sum(RL.SCORE),0) useful " +
                "from REVIEWS " +
                "left join PUBLIC.REVIEWS_LIKES RL on REVIEWS.REVIEW_ID = RL.REVIEW_ID " +
                "group by REVIEWS.REVIEW_ID " +
                "ORDER BY useful DESC " +
                "LIMIT :count";
        return jdbc.query(sql, Map.of("count", count), reviewRowMapper);
    }

    @Override
    public void addLikeOrDisLike(Long id, Long userId, boolean like) {
        int score;
        if (like) {
            score = 1;
        } else {
            score = -1;
        }
        String sql = "MERGE INTO REVIEWS_LIKES (REVIEW_ID, USER_ID, SCORE) VALUES ( :id,:userId,:score )";
        jdbc.update(sql, Map.of("id", id, "userId", userId, "score", score));
    }


    @Override
    public void removeLikeOrDislike(Long id, Long userId) {
        String sql = "DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID = :id AND USER_ID = :userId";
        jdbc.update(sql, Map.of("id", id, "userId", userId));
    }

    private SqlParameterSource getParams(Review review) {
        return new MapSqlParameterSource().addValue("content", review.getContent())
                .addValue("isPositive", review.getIsPositive())
                .addValue("userId", review.getUserId())
                .addValue("filmId", review.getFilmId())
                .addValue("useful", review.getUseful())
                .addValue("id", review.getId());
    }
}
