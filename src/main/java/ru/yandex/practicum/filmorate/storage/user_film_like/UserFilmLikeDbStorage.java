package ru.yandex.practicum.filmorate.storage.user_film_like;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.UserFilmLike;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
public class UserFilmLikeDbStorage extends BaseDbStorage<UserFilmLike> implements UserFilmLikeStorage {
    private static final String GET_BY_FILM_ID_QUERY = "SELECT user_id FROM user_film_like WHERE film_id = ?";
    private static final String GET_BY_FILMS_IDS_QUERY =
            "SELECT film_id, user_id FROM user_film_like WHERE film_id IN (%s)";
    private static final String INSERT_QUERY = "INSERT INTO user_film_like (user_id, film_id) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM user_film_like WHERE user_id = ? AND film_id = ?";

    public UserFilmLikeDbStorage(JdbcTemplate jdbc, RowMapper<UserFilmLike> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Long> getLikesByFilmId(Long filmId) {
        return getSimpleList(GET_BY_FILM_ID_QUERY, Long.class, filmId);
    }

    @Override
    public Map<Long, Set<Long>> getLikesByFilmsIds(Collection<Long> filmsIds) {
        if (filmsIds == null || filmsIds.isEmpty()) {
            return Map.of();
        }

        String placeholders = filmsIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String query = String.format(GET_BY_FILMS_IDS_QUERY, placeholders);

        List<Object> args = new ArrayList<>(filmsIds);

        return jdbc.query(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(query);
                    int index = 1;

                    for (Object arg : args) {
                        ps.setObject(index++, arg);
                    }

                    return ps;
                },
                rs -> {
                    Map<Long, Set<Long>> likesMap = new HashMap<>();

                    while (rs.next()) {
                        Long filmId = rs.getLong("film_id");
                        Long userId = rs.getLong("user_id");

                        likesMap.computeIfAbsent(filmId, key -> new HashSet<>()).add(userId);
                    }

                    return likesMap;
                }
        );
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