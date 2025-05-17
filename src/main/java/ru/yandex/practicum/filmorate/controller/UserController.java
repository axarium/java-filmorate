package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.IdGenerator;
import ru.yandex.practicum.filmorate.validator.OnCreate;
import ru.yandex.practicum.filmorate.validator.OnUpdate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private long usersCount = 0;

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Process GET /users request");

        return users.values();
    }

    @PostMapping
    public User createUser(@Validated(OnCreate.class) @RequestBody User user) {
        log.info("Process POST /users request with user {}", user);

        updateUserName(user);
        usersCount = IdGenerator.generateId(usersCount);
        user.setId(usersCount);
        users.put(user.getId(), user);

        return user;
    }

    @PutMapping
    public User updateUser(@Validated(OnUpdate.class) @RequestBody User user) {
        log.info("Process PUT /users request with user {}", user);

        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь не найден.");
        }

        updateUserName(user);
        users.put(user.getId(), user);

        return user;
    }

    private void updateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}