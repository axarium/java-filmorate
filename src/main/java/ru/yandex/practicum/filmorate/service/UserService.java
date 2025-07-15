package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

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
        updateUserName(user);
        return userStorage.updateUser(user);
    }

    public User addInFriends(Long id, Long friendId) {
        User user = userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь для добавления в друзья не найден."));

        friendshipStorage.addFriendship(user.getId(), friend.getId());

        return user;
    }

    public void deleteFromFriends(Long id, Long friendId) {
        User user = userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь для удаления из друзей не найден."));

        friendshipStorage.deleteFriendship(user.getId(), friend.getId());
    }

    public Collection<User> getFriends(Long id) {
        User user = userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        return friendshipStorage.getFriendsByUserId(user.getId());
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        User user = userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Первый пользователь не найден."));
        User otherUser = userStorage.getUserById(otherId)
                .orElseThrow(() -> new NotFoundException("Второй пользователь не найден."));

        Collection<User> firstFriends = friendshipStorage.getFriendsByUserId(user.getId());
        Collection<User> secondFriends = friendshipStorage.getFriendsByUserId(otherUser.getId());

        return firstFriends.stream()
                .filter(secondFriends::contains)
                .collect(Collectors.toList());
    }

    private void updateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}