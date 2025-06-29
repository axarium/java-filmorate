package ru.yandex.practicum.filmorate.storage.film_genre;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.Collection;

@Repository
@Primary
public class FilmGenreDbStorage extends BaseDbStorage<FilmGenre> implements FilmGenreStorage {
    private static final String GET_BY_FILM_ID_QUERY = "SELECT * FROM film_genre WHERE film_id = ? ORDER BY genre_id";
    private static final String INSERT_QUERY = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

    public FilmGenreDbStorage(JdbcTemplate jdbc, RowMapper<FilmGenre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<FilmGenre> getFilmGenresByFilmId(Long filmId) {
        return getMany(GET_BY_FILM_ID_QUERY, filmId);
    }

    @Override
    public void addFilmGenre(Long filmId, Long genreId) {
        insert(INSERT_QUERY, filmId, genreId);
    }
}