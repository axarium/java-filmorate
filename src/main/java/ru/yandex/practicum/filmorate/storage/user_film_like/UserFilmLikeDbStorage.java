package ru.yandex.practicum.filmorate.storage.user_film_like;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.UserFilmLike;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.Collection;

@Repository
@Primary
public class UserFilmLikeDbStorage extends BaseDbStorage<UserFilmLike> implements UserFilmLikeStorage {
    private static final String GET_BY_FILM_ID_QUERY = "SELECT user_id FROM user_film_like WHERE film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO user_film_like (user_id, film_id) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM user_film_like WHERE user_id = ? AND film_id = ?";

    public UserFilmLikeDbStorage(JdbcTemplate jdbc, RowMapper<UserFilmLike> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Long> getUsersIdsByFilmId(Long filmId) {
        return getSimpleList(GET_BY_FILM_ID_QUERY, Long.class, filmId);
    }

    @Override
    public void addLike(Long userId, Long filmId) {
        insert(INSERT_QUERY, userId, filmId);
    }

    @Override
    public void deleteLike(Long userId, Long filmId) {
        delete(DELETE_QUERY, userId, filmId);
    }
}