package ru.yandex.practicum.filmorate.storage.friendship;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.*;

@Repository
@Primary
public class FriendshipDbStorage extends BaseDbStorage<User> implements FriendshipStorage {
    private static final String INSERT_QUERY =
            "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_QUERY =
            "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
    private static final String GET_FRIENDS_BY_USER_ID_QUERY = """
                SELECT u.*
                FROM app_user u
                JOIN friendship f ON u.id = f.friend_id
                WHERE f.user_id = ?
            """;

    public FriendshipDbStorage(JdbcTemplate jdbc, RowMapper<User> userMapper) {
        super(jdbc, userMapper);
    }

    @Override
    public Collection<User> getFriendsByUserId(Long userId) {
        return getMany(GET_FRIENDS_BY_USER_ID_QUERY, userId);
    }

    @Override
    public void addFriendship(Long userId, Long friendId) {
        insert(INSERT_QUERY, userId, friendId);
    }

    @Override
    public void deleteFriendship(Long userId, Long friendId) {
        delete(DELETE_QUERY, userId, friendId);
    }
}