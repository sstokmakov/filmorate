package ru.tokmakov.filmorate.repository.user.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class UserExtractor implements ResultSetExtractor<User> {

    @Override
    public User extractData(ResultSet rs) throws SQLException, DataAccessException {
        User user = null;
        Set<Long> friendsIds = new HashSet<>();
        while (rs.next()) {
            if (user == null) {
                user = new User();
                user.setId(rs.getInt("USERS.user_id"));
                user.setEmail(rs.getString("USERS.email"));
                user.setLogin(rs.getString("USERS.login"));
                user.setName(rs.getString("USERS.name"));
                LocalDate birthdayLocalDate = Objects.requireNonNull(rs.getDate("USERS.birthday")).toLocalDate();
                user.setBirthday(birthdayLocalDate);
            }
            friendsIds.add(rs.getLong("FRIENDS.friend_id"));
        }
        if (friendsIds.size() > 0) {
            user.setFriendsSet(friendsIds);
        }
        return user;
    }

}
