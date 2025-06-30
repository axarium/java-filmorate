package ru.yandex.practicum.filmorate.storage.friendship;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FriendshipStorage {

    Collection<User> getFriendsByUserId(Long userId);

    void addFriendship(Long fromUserId, Long toUserId);

    void deleteFriendship(Long userId, Long friendId);
}