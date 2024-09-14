package ru.tokmakov.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.LinkedHashSet;


/**
 * Film.
 */
@Data
@EqualsAndHashCode(of = "id")
public class Film {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration; // Длительность в минутах
    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();
    private LinkedHashSet<Director> directors = new LinkedHashSet<>();
    private MPARating mpa;
}
