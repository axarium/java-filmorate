package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
@Primary
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {
    private static final String GET_ALL_QUERY = "SELECT * FROM genre ORDER BY id";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return getMany(GET_ALL_QUERY);
    }

    @Override
    public Optional<Genre> getGenreById(Long id) {
        return getOne(GET_BY_ID_QUERY, id);
    }
}