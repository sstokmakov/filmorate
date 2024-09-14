package ru.tokmakov.filmorate.service.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.filmorate.exception.ValidationException;
import ru.filmorate.model.Director;
import ru.filmorate.model.Film;
import ru.filmorate.model.Review;
import ru.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

@Slf4j
@Service
public class ValidationServiceImpl implements ValidationService {
    public static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    @Override
    public void validateNewData(User user) {
        if (!user.getEmail().contains("@") || user.getEmail().isBlank()) {
            log.error("Неверный формат электронной почты");
            throw new ValidationException("Неверный формат электронной почты");
        }
        if (user.getLogin().isBlank()) {
            throw new ValidationException("Неверный формат логина");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    @Override
    public void validateNewData(Review review) {
        if (review.getContent() == null || review.getContent().isEmpty()) {
            throw new ValidationException("Отзыв не может быть пустым");
        }
        if (review.getUserId() == 0) {
            throw new ValidationException("Не указан ID пользователя для отзыва");
        }
        if (review.getFilmId() == 0) {
            throw new ValidationException("Не указан ID фильма для отзыва");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Не указан тип отзыва");
        }
    }

    //FILM VALIDATIONS

    @Override
    public void validateNewData(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Название фильма - пустое.");
            throw new ValidationException("Название фильма не должно быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Описание фильма - более 200 символов.");
            throw new ValidationException("Максимальная длина описания фильма - не более 200 символов.");
        }

        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            log.warn("Дата релиза фильма - раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза фильма - не раньше 28 декабря 1895 года.");
        }

        if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма - отрицательное или нулевой значение");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
    }

    @Override
    public void validateNewData(Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            log.warn("Имя режиссера - пустое.");
            throw new ValidationException("Имя режиссера не должно быть пустым.");
        }
    }

}
