package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseDbStorage<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected Optional<T> getOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> getMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }

    protected <E> List<E> getSimpleList(String query, Class<E> type, Object... params) {
        return jdbc.queryForList(query, type, params);
    }

    protected Long insertWithGeneratedKey(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

                    for (int idx = 0; idx < params.length; idx++) {
                        ps.setObject(idx + 1, params[idx]);
                    }

                    return ps;
                },
                keyHolder
        );

        return keyHolder.getKeyAs(Long.class);
    }

    protected void insert(String query, Object... params) {
        jdbc.update(query, params);
    }

    protected void update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);

        if (rowsUpdated == 0) {
            throw new NotFoundException("Не найден объект для обновления.");
        }
    }

    protected void delete(String query, Object... params) {
        jdbc.update(query, params);
    }
}