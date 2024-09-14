package ru.tokmakov.filmorate.repository.director;

import ru.filmorate.model.Director;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;

public interface DirectorRepository {

    Optional<Director> getById(long directorId);

    Director save(Director director);

    Director update(Director director);

    void deleteById(long directorId);

    Collection<Director> getAll();

    LinkedHashSet<Director> getDirectors(long filmId);

}
