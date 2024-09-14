package ru.tokmakov.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
public class Review {
    @JsonProperty("reviewId")
    private long id;
    private String content;
    private Boolean isPositive;
    private long userId;
    private long filmId;
    private int useful;
}
