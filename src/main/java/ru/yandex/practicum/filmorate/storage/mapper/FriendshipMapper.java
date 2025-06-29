package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendshipMapper implements RowMapper<Friendship> {

    @Override
    public Friendship mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Friendship friendship = new Friendship();
        friendship.setUserId(resultSet.getLong("user_id"));
        friendship.setFriendId(resultSet.getLong("friend_id"));
        friendship.setFriendshipStatusId(resultSet.getLong("friendship_status_id"));

        return friendship;
    }
}