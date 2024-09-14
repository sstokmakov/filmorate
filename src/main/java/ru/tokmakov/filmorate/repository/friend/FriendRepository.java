package ru.tokmakov.filmorate.repository.friend;

import java.util.Set;

public interface FriendRepository {

    void add(long userId, long friendId);

    void delete(long userId, long friendId);

    Set<Long> getFriendsIds(long userId);
}
