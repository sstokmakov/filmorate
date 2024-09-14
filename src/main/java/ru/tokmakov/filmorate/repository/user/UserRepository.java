package ru.tokmakov.filmorate.repository.user;

import ru.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {

    Optional<User> getById(long userId);

    User save(User user);

    void deleteById(Long userId);

    User update(User user);

    Collection<User> getFriendsByID(Long userId);

    Collection<User> getAll();

    Collection<User> getCommonFriends(long userId1, long userId2);

    Set<Long> getAllLikes(long userId);
}
