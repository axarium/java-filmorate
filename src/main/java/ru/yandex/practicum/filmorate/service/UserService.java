package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    public Collection<User> getAllUsers() {
        Collection<User> users = userStorage.getAllUsers();

        for (User user : users) {
            user.getFriendsIds().addAll(friendshipStorage.getFriendsIdsByUserId(user.getId()));
        }

        return users;
    }

    public User getUserById(Long id) {
        User user = userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        user.getFriendsIds().addAll(friendshipStorage.getFriendsIdsByUserId(user.getId()));
        return user;
    }

    public User createUser(User user) {
        updateUserName(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        updateUserName(user);
        user.getFriendsIds().addAll(friendshipStorage.getFriendsIdsByUserId(user.getId()));
        return userStorage.updateUser(user);
    }

    public User addInFriends(Long id, Long friendId) {
        User user = userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь для добавления в друзья не найден."));
        Optional<Friendship> reverseFriendship = friendshipStorage.getFriendship(friend.getId(), user.getId());

        if (reverseFriendship.isPresent()) {
            friendshipStorage.addFriendship(user.getId(), friend.getId(), FriendshipStorage.CONFIRMED);
            friendshipStorage.updateFriendshipStatus(friend.getId(), user.getId(), FriendshipStorage.CONFIRMED);
        } else {
            friendshipStorage.addFriendship(user.getId(), friend.getId(), FriendshipStorage.PENDING);
        }

        user.getFriendsIds().addAll(friendshipStorage.getFriendsIdsByUserId(user.getId()));

        return user;
    }

    public void deleteFromFriends(Long id, Long friendId) {
        User user = userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь для удаления из друзей не найден."));
        friendshipStorage.deleteFriendship(user.getId(), friend.getId());

        Optional<Friendship> reverseFriendship = friendshipStorage.getFriendship(friend.getId(), user.getId());

        if (reverseFriendship.isPresent() &&
                !Objects.equals(reverseFriendship.get().getFriendshipStatusId(), FriendshipStorage.PENDING)) {
            friendshipStorage.updateFriendshipStatus(friend.getId(), user.getId(), FriendshipStorage.PENDING);
        }
    }

    public Collection<User> getFriends(Long id) {
        User user = userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        Collection<User> friends = friendshipStorage.getFriendsIdsByUserId(user.getId())
                .stream()
                .map(userStorage::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        for (User friend : friends) {
            friend.getFriendsIds().addAll(friendshipStorage.getFriendsIdsByUserId(friend.getId()));
        }

        return friends;
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        User user = userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Первый пользователь не найден."));
        User otherUser = userStorage.getUserById(otherId)
                .orElseThrow(() -> new NotFoundException("Второй пользователь не найден."));
        Set<Long> firstFriendsIds = new HashSet<>(friendshipStorage.getFriendsIdsByUserId(user.getId()));
        Set<Long> secondFriendsIds = new HashSet<>(friendshipStorage.getFriendsIdsByUserId(otherUser.getId()));

        firstFriendsIds.retainAll(secondFriendsIds);

        Collection<User> commonFriends = firstFriendsIds.stream()
                .map(userStorage::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        for (User friend : commonFriends) {
            friend.getFriendsIds().addAll(friendshipStorage.getFriendsIdsByUserId(friend.getId()));
        }

        return commonFriends;
    }

    private void updateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}