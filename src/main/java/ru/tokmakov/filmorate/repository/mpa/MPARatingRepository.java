package ru.tokmakov.filmorate.repository.mpa;

import ru.filmorate.model.MPARating;

import java.util.Collection;

public interface MPARatingRepository {

    Collection<MPARating> getAllMPARatings();

    MPARating getMPARatingById(int mpaId);
}
