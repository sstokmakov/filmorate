package ru.tokmakov.filmorate.repository.review;

import ru.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    Review create(Review review);

    Review update(Review review);

    void delete(Long id);

    Optional<Review> getById(Long id);

    List<Review> getByFilmLimit(Long filmId, int count);

    List<Review> getByFilmLimit(int count);

    void addLikeOrDisLike(Long id, Long userId, boolean like);

    void removeLikeOrDislike(Long id, Long userId);

}
