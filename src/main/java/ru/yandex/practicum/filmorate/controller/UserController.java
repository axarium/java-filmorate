package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping
    public ResponseEntity<Collection<User>> getAllUsers() {
        Collection<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        User user = userService.addFriend(id, friendId);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.debug("Вызов метода removeFriend c параметрами:id={},friendId={}", id, friendId);
        User result = userService.removeFriend(id, friendId);
        log.debug("Пользователь с id={} удалил из друзей пользователя с id={} ", id, friendId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Collection<User>> getFriends(@PathVariable Long id) {
        log.debug("Вызов метода getFriends c параметрами:id={}", id);
        Set<User> result = userService.getFriends(id);
        log.debug("Возвращен список друзей пользователя с id={} ", id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Collection<User>> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.debug("Вызов метода getCommonFriends c параметрами:id={},otherId={}", id, otherId);
        Collection<User> result = userService.getCommonFriends(id, otherId);
        log.debug("Возвращен список общих друзей пользователей с id={},otherId={} ", id, otherId);
        return ResponseEntity.ok(result);
    }

}
