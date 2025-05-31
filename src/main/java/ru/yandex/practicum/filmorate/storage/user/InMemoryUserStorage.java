package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public void save(User user) {
        log.debug("Попытка сохранить пользователя с id {}", user.getId());
        user.setId(generateId());
        users.put(user.getId(), user);
        log.debug("Пользователь с id={} успешно создан", user.getId());
    } // Комментарий

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        log.debug("Попытка вызвать список всех пользователей");
        return users.values();
    }

    @Override
    public Optional<User> findById(Long id) {
        log.debug("Попытка найти пользователя с id={}", id);
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Попытка удалить пользователя с id={}", id);
        users.remove(id);
    }

    private long generateId() {
        log.debug("Попытка сгенерировать ID");
        long maxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
        log.debug("Сгенерировано ID  пользователя {}", maxId);
        return ++maxId;
    }
}