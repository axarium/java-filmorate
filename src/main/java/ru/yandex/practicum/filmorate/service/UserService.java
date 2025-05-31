package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Scope
public class UserService {

    private final UserStorage userStorage;

    public User createUser(User user) {
        log.debug("Попытка создать пользователя с именем {}", user.getName());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя не задано, установлено имя логина {}", user.getLogin());

        }
        userStorage.save(user);
        log.debug("Пользователь с id={} успешно создан", user.getId());
        return user;
    }

    public User updateUser(User user) {

        log.trace("Запрос на обновление пользователя ID={}", user.getId());

        if (user.getId() == null || user.getId() <= 0) {
            throw new ValidationException("ID не указан");
        }
        Optional<User> existingUser = userStorage.findById(user.getId());
        if (existingUser.isEmpty()) {
            log.error("Пользователь с ID={} не найден", user.getId());
            throw new NotFoundException("Пользователь не найден");
        }
        User updatedUser = userStorage.update(user);
        log.info("Пользователь с ID={} успешно обновлён", user.getId());
        return updatedUser;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addFriend(Long userId, Long friendId) {

        log.debug("Попытка пользователя id={} добавить в друзья пользователя с id={}", userId, friendId);

        User user = getUserOrThrow(userId);
        log.debug("Найден пользователь с id={} метод addFriend", userId);

        User friend = getUserOrThrow(friendId);
        log.debug("Найден пользователь для добавления в друзья с id={}", friendId);

        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить себя в друзья");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        log.debug("Пользователи {} и {} теперь друзья", userId, friendId);
        return user;
    }

    public User removeFriend(Long userId, Long friendId) {
        log.debug("Попытка пользователя id={} удалить из друзей пользователя с id={}", userId, friendId);

        User user = getUserOrThrow(userId);
        log.debug("Найден пользователь с id={} метод removeFriend", userId);

        User friend = getUserOrThrow(friendId);
        log.debug("Найден пользователь для удаления из друзей с id={}", friendId);


        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.info("Пользователи {} и {} больше не друзья", userId, friendId);
        return user;
    }

    public Set<User> getFriends(Long userId) {
        log.debug("Получение списка друзей пользователя с id={}", userId);

        User user = getUserOrThrow(userId);
        log.debug("Найден пользователь с id={} метод getFriends", userId);

        Set<User> friends = user.getFriends().stream()
                .map(userStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        log.debug("Найдено {} друзей для пользователя с id={}", friends.size(), userId);
        return friends;
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        log.debug("Получение списка общих друзей пользователей с id={} и id={}", userId, otherId);

        User user = getUserOrThrow(userId);
        User other = getUserOrThrow(otherId);

        log.debug("Пользователи найдены: {} и {}", userId, otherId);

        Set<Long> commonIds = new HashSet<>(user.getFriends());
        commonIds.retainAll(other.getFriends());

        log.debug("Количество общих друзей: {}", commonIds.size());

        List<User> commonFriends = commonIds.stream()
                .map(userStorage::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        log.debug("Общих друзей получено {}", commonFriends.size());
        return commonFriends;
    }

    public Optional<User> findUserById(Long id) {
        return userStorage.findById(id);
    }

    public User getUserOrThrow(Long id) {
        log.debug("Попытка найти пользователя с id={}", id);
        User user = userStorage.findById(id).orElseThrow(() ->
                new NotFoundException("Пользователь с id= " + id + " не найден"));

        log.debug("Пользователь с id={} найден", id);
        return user;
    }

}