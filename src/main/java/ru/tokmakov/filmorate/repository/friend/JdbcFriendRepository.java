package ru.tokmakov.filmorate.repository.friend;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JdbcFriendRepository implements FriendRepository {
    private final NamedParameterJdbcOperations jdbcFr;

    @Override
    public void add(long userId, long friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id) " +
                "VALUES (:userId, :friendId)";
        jdbcFr.update(sql, Map.of("userId", userId,
                "friendId", friendId));
    }

    @Override
    public void delete(long userId, long friendId) {
        String sql = "DELETE FROM FRIENDS " +
                "WHERE (user_id = :userId AND friend_id= :friendId)";
        jdbcFr.update(sql, Map.of(
                "userId", userId,
                "friendId", friendId));
    }

    @Override
    public Set<Long> getFriendsIds(long userId) {
        String sql1 =
                "SELECT friend_id FROM friends " +
                        "WHERE user_id = :userId";
        return new HashSet<>(jdbcFr.queryForList(sql1, Map.of(
                "userId", userId), Long.class));
    }
}
