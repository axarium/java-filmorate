package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
    }

    public User createUser(User user) {
        updateUserName(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        if (userStorage.getUserById(user.getId()).isEmpty()) {
            throw new NotFoundException("Пользователь не найден.");
        }

        updateUserName(user);

        return userStorage.updateUser(user);
    }

    public User addInFriends(Long id, Long friendId) {
        User user = userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь для добавления в друзья не найден."));
        user.getFriendsIds().add(friendId);
        friend.getFriendsIds().add(id);
// Тест
        return user;
    }

    public void deleteFromFriends(Long id, Long friendId) {
        User user = userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь для добавления в друзья не найден."));
        user.getFriendsIds().remove(friendId);
        friend.getFriendsIds().remove(id);
    }

    public Collection<User> getFriends(Long id) {
        User user = userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        return user.getFriendsIds()
                .stream()
                .map(userStorage::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        User user = userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Первый пользователь не найден."));
        User otherUser = userStorage.getUserById(otherId)
                .orElseThrow(() -> new NotFoundException("Второй пользователь не найден."));

        return user.getFriendsIds()
                .stream()
                .filter(friendId -> otherUser.getFriendsIds().contains(friendId))
                .map(userStorage::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private void updateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}