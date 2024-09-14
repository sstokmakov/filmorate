package ru.tokmakov.filmorate.service.director;

import ru.filmorate.model.Director;

import java.util.Collection;
import java.util.LinkedHashSet;

public interface DirectorService {

    Collection<Director> getAll();

    Director getById(long directorId);

    Director save(Director director);

    Director update(Director director);

    void deleteById(long id);

    LinkedHashSet<Director> getDirectors(long filmId);


}