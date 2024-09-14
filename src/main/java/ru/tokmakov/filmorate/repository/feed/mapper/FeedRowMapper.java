package ru.tokmakov.filmorate.repository.feed.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.filmorate.model.EventOperation;
import ru.filmorate.model.EventType;
import ru.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedRowMapper implements RowMapper<Feed> {
    @Override
    public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
        Feed feed = new Feed();
        feed.setId(rs.getInt("event_id"));
        feed.setTimestamp(rs.getDate("timestamp"));
        feed.setEventType(EventType.valueOf(rs.getString("event_type")));
        feed.setOperation(EventOperation.valueOf(rs.getString("operation")));
        feed.setUserId(rs.getInt("user_id"));
        feed.setEntityId(rs.getInt("entity_id"));
        return feed;
    }
}
