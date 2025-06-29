package ru.yandex.practicum.filmorate.storage.friendship;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
@Primary
public class FriendshipDbStorage extends BaseDbStorage<Friendship> implements FriendshipStorage {
    private static final String GET_BY_USER_ID_QUERY = "SELECT friend_id FROM friendship WHERE user_id = ?";
    private static final String GET_BY_USER_ID_AND_FRIEND_ID_QUERY =
            "SELECT * FROM friendship WHERE user_id = ? AND friend_id = ?";
    private static final String INSERT_QUERY =
            "INSERT INTO friendship (user_id, friend_id, friendship_status_id) VALUES (?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE friendship SET friendship_status_id = ? WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";

    public FriendshipDbStorage(JdbcTemplate jdbc, RowMapper<Friendship> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Long> getFriendsIdsByUserId(Long userId) {
        return getSimpleList(GET_BY_USER_ID_QUERY, Long.class, userId);
    }

    @Override
    public Optional<Friendship> getFriendship(Long userId, Long friendId) {
        return getOne(GET_BY_USER_ID_AND_FRIEND_ID_QUERY, userId, friendId);
    }

    @Override
    public void addFriendship(Long userId, Long friendId, Long friendshipStatusId) {
        insert(INSERT_QUERY, userId, friendId, friendshipStatusId);
    }

    @Override
    public void updateFriendshipStatus(Long userId, Long friendId, Long newFriendshipStatusId) {
        update(UPDATE_QUERY, userId, friendId, newFriendshipStatusId);
    }

    @Override
    public void deleteFriendship(Long userId, Long friendId) {
        delete(DELETE_QUERY, userId, friendId);
    }
}