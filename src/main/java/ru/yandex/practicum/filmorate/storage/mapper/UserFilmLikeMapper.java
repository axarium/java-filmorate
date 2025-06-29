package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserFilmLike;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserFilmLikeMapper implements RowMapper<UserFilmLike> {

    @Override
    public UserFilmLike mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        UserFilmLike userFilmLike = new UserFilmLike();
        userFilmLike.setUserId(resultSet.getLong("user_id"));
        userFilmLike.setFilmId(resultSet.getLong("film_id"));

        return userFilmLike;
    }
}