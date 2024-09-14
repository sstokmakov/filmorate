package ru.tokmakov.filmorate.repository.like;

import java.util.Set;

public interface LikeRepository {

    void add(long userId, long filmId);

    void delete(long userId, long filmId);

    Set<Long> findAllFilmLikes(long filmId);
}
