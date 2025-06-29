package ru.yandex.practicum.filmorate.storage.friendship;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Collection;
import java.util.Optional;

public interface FriendshipStorage {
    Long PENDING = 1L;
    Long CONFIRMED = 2L;

    Collection<Long> getFriendsIdsByUserId(Long userId);

    Optional<Friendship> getFriendship(Long userId, Long friendId);

    void addFriendship(Long fromUserId, Long toUserId, Long friendshipStatusId);

    void updateFriendshipStatus(Long userId, Long friendId, Long newFriendshipStatusId);

    void deleteFriendship(Long userId, Long friendId);
}