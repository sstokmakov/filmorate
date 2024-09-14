package ru.tokmakov.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(of = "id")
public class Feed {
    @JsonProperty("eventId")
    private long id;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date timestamp;
    private EventType eventType;
    private EventOperation operation;
    private long entityId;
    private long userId;
}
