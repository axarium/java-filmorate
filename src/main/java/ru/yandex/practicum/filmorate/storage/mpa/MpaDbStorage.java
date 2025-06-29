package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
@Primary
public class MpaDbStorage extends BaseDbStorage<Mpa> implements MpaStorage {
    private static final String GET_ALL_QUERY = "SELECT * FROM mpa ORDER BY id";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM mpa WHERE id = ?";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        return getMany(GET_ALL_QUERY);
    }

    @Override
    public Optional<Mpa> getMpaById(Long id) {
        return getOne(GET_BY_ID_QUERY, id);
    }
}