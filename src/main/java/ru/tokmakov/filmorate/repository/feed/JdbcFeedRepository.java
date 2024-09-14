package ru.tokmakov.filmorate.repository.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.filmorate.model.Feed;
import ru.filmorate.repository.feed.mapper.FeedRowMapper;

import java.util.Collection;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcFeedRepository implements FeedRepository {
    private final FeedRowMapper feedRowMapper;
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Collection<Feed> getAllByUser(long userId) {
        String sql = "SELECT * FROM FEEDS " +
                "WHERE USER_ID = :userId";
        log.info("==> Get /feed Запрос ленты новосте юзера {} ", userId);
        return jdbc.query(sql, Map.of("userId", userId), feedRowMapper);
    }

    @Override
    public void add(Feed feed) {
        String sql = "INSERT INTO FEEDS (USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) " +
                "VALUES (:userId, :eventType, :operation, :entityId)";
        Map<String, Object> params = Map.of("userId", feed.getUserId(),
                "eventType", feed.getEventType().toString(),
                "operation", feed.getOperation().toString(),
                "entityId", feed.getEntityId());
        jdbc.update(sql, params);
        log.info("==> PUT /feed Добвалена новость - {} ", feed);
    }
}
