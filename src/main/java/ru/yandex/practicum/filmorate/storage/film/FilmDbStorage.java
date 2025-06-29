package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
@Primary
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String GET_ALL_QUERY = "SELECT * FROM film";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM film WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO film (name, description, release_date, duration, "
            + "mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE film SET name = ?, description = ?, release_date = ?"
            + ", duration = ?, mpa_id = ? WHERE id = ?";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return getMany(GET_ALL_QUERY);
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return getOne(GET_BY_ID_QUERY, id);
    }

    @Override
    public Film createFilm(Film film) {
        Long id = insertWithGeneratedKey(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null
        );
        film.setId(id);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        return film;
    }
}