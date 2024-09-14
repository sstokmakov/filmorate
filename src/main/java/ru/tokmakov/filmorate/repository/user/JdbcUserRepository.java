package ru.tokmakov.filmorate.repository.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.filmorate.exception.NotFoundException;
import ru.filmorate.model.User;
import ru.filmorate.repository.friend.FriendRepository;
import ru.filmorate.repository.user.mapper.UserExtractor;
import ru.filmorate.repository.user.mapper.UserRowMapper;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class

JdbcUserRepository implements UserRepository {


    private final NamedParameterJdbcOperations jdbc;
    private final FriendRepository friendsRepository;

    @Override
    public Optional<User> getById(long userId) {
        final String sql1 = "SELECT * FROM USERS " +
                "LEFT JOIN FRIENDS ON USERS.USER_ID = FRIENDS.USER_ID " +
                "WHERE USERS.USER_ID = :userId";
        User user = jdbc.query(sql1, Map.of("userId", userId), new UserExtractor());
        System.out.println("Возврат репозитория: " + user);
        return Optional.ofNullable(user);
    }

    @Override
    public Collection<User> getFriendsByID(Long userId) {
        final String sql1 = "SELECT * FROM USERS, FRIENDS WHERE USERS.USER_ID = :userId " +
                "AND FRIENDS.USER_ID = :userId";
        final String sql = "SELECT * FROM USERS WHERE USER_ID in " +
                "(SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = :userId)";
        return jdbc.query(sql, Map.of("userId", userId), new UserRowMapper());
    }


    @Override
    public User save(User user) {
        final String sql = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) " +
                "VALUES (:email, :login, :name, :birthday)";

        GeneratedKeyHolder keyHolderUser = new GeneratedKeyHolder();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday());
        jdbc.update(sql, params, keyHolderUser);
        user.setId(keyHolderUser.getKeyAs(Long.class));
        log.info("В хранилище сохранен user с id = " + user.getId());
        return user;
    }

    @Override
    public void deleteById(Long userId) {
        final String query = "DELETE FROM USERS WHERE USER_ID = :userId";
        jdbc.update(query, Map.of("userId", userId));
    }

    @Override
    public Collection<User> getAll() {
        final String sqlUsers = "SELECT * FROM USERS";
        Collection<User> users = jdbc.query(sqlUsers, new UserRowMapper());

        return users;
    }

    @Override
    public User update(User user) {
        final String sqlUpdate = "UPDATE USERS " +
                "SET EMAIL = :email, " +
                "LOGIN = :login, NAME = :username, " +
                "BIRTHDAY = :birthday " +
                "WHERE USER_ID = :userId";
        jdbc.update(sqlUpdate, Map.of("userId", user.getId(),
                "email", user.getEmail(),
                "login", user.getLogin(),
                "username", user.getName(),
                "birthday", user.getBirthday()));
        Optional<User> userFromDb = getById(user.getId());
        if (userFromDb.isPresent()) {
            userFromDb.get().setFriendsSet(
                    friendsRepository.getFriendsIds(user.getId()));
            return userFromDb.get();
        } else {
            throw new NotFoundException("После обновления, пользователь c id= " + user.getId() + " не найден");
        }

    }

    @Override
    public Collection<User> getCommonFriends(long userId1, long userId2) {
//        final String sql = "SELECT * FROM USERS u, FRIENDS f, FRIENDS o "
//                           + "WHERE u.USER_ID = f.FRIEND_ID AND u.USER_ID = o.FRIEND_ID AND f.USER_ID = :userId AND o.USER_ID = :userId";
        final String sql1 = "SELECT * from USERS AS u WHERE USER_ID IN " +
                "(SELECT FRIEND_ID FROM USERS AS u JOIN FRIENDS AS f ON u.USER_ID = f.USER_ID WHERE u.USER_ID = :userId1)" +
                "AND USER_ID IN " +
                "(SELECT FRIEND_ID FROM USERS AS u JOIN FRIENDS AS f ON u.USER_ID = f.USER_ID WHERE u.USER_ID = :userId2)";
        return jdbc.query(sql1, Map.of("userId1", userId1, "userId2", userId2), new UserRowMapper());
    }

    public Set<Long> getAllLikes(long userId) {
        final String sql = "SELECT FILM_ID FROM LIKES WHERE USER_ID = :userId";
        return new HashSet<>(jdbc.queryForList(sql, Map.of(
                "userId", userId
        ), Long.class));
    }
}
