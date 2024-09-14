package ru.tokmakov.filmorate.service.validation;

import org.springframework.stereotype.Service;
import ru.filmorate.model.Director;
import ru.filmorate.model.Film;
import ru.filmorate.model.Review;
import ru.filmorate.model.User;

@Service
public interface ValidationService {
    void validateNewData(Film film);

    void validateNewData(User user);

    void validateNewData(Review review);

    void validateNewData(Director director);
}
