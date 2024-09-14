package ru.tokmakov.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.filmorate.model.Feed;
import ru.filmorate.model.Film;
import ru.filmorate.model.User;
import ru.filmorate.service.feed.FeedService;
import ru.filmorate.service.user.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FeedService feedService;

    @GetMapping("/users")
    public Collection<User> getAll() {
        log.info("==> GET /users ");
        Collection<User> allUsers = userService.getAll();
        log.info("<== GET /users Список пользователей размером: "
                + allUsers.size() + " возвращен");
        return allUsers;
    }

    @GetMapping("/users/{id}")
    public User get(@PathVariable long id) {
        log.info("==> GET /users/" + id);
        User user = userService.getById(id);
        log.info("<== GET /users/" + id + "  Пользователь: " + user);
        return user;
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> getFriends(@PathVariable long id) {
        log.info("==> GET /users/" + id + "/friends");
        Collection<User> userFriends = userService.getFriendsById(id);
        System.out.println(userFriends);
        log.info("<== GET /users/" + id + "/friends" + " Количество друзей: " + userFriends.size());
        return userFriends;
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("==> GET /users/" + id + "/friends/common/" + otherId);
        Collection<User> usersCommonFriends = userService.getCommonFriends(id, otherId);
        log.info("<== GET /users/" + id + "/friends/common/" + otherId +
                " Количество общих друзей: " + usersCommonFriends.size());
        return usersCommonFriends;
    }

    @PostMapping("/users")
    public User save(@RequestBody User user) {
        log.info("==> POST /users " + user);
        User newUser = userService.create(user);
        log.info("<== POST /users " + newUser);
        return newUser;
    }

    @PutMapping("/users")
    public User update(@RequestBody User user) {
        log.info("==> PUT /users " + user);
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User updatedUser = userService.update(user);
        log.info("<== PUT /users " + updatedUser);
        return updatedUser;
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("==> PUT /users/" + id + "/friends/" + friendId);
        userService.addFriend(id, friendId);
        log.info("Добавлена запись дружбы пользователя с id=" + id + " с пользователем с id=" + friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("==> DELETE /users/" + id + "/friends/" + friendId);
        userService.deleteFriend(id, friendId);
        log.info("Удалена запись дружбы пользователя с id=" + id + " с пользователем с id=" + friendId);
    }

    @GetMapping("/users/{id}/recommendations")
    public List<Film> recommendFilms(@PathVariable("id") long userId) {
        return userService.recommendFilms(userId);
    }

    @GetMapping("/users/{id}/likes")
    public List<Long> findAllFilmLikes(@PathVariable("id") long userId) {
        return userService.findAllFilmLikes(userId);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("==> DELETE /users/" + userId);
        userService.deleteById(userId);
        log.info("Удален пользовател с id=" + userId);
    }

    @GetMapping("/users/{id}/feed")
    public Collection<Feed> getFeeds(@PathVariable("id") long userId) {
        return feedService.getFeeds(userId);
    }
}
