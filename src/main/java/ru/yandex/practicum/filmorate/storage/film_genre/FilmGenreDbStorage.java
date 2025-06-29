package ru.yandex.practicum.filmorate.storage.film_genre;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
public class FilmGenreDbStorage extends BaseDbStorage<FilmGenre> implements FilmGenreStorage {
    private static final String GET_BY_FILM_ID_QUERY = "SELECT * FROM film_genre WHERE film_id = ? ORDER BY genre_id";
    private static final String INSERT_QUERY = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String GET_GENRES_BY_FILMS_IDS_QUERY = """
        SELECT fg.film_id, g.id AS genre_id, g.name AS genre_name
        FROM film_genre fg
        JOIN genre g ON fg.genre_id = g.id
        WHERE fg.film_id IN (%s)
        ORDER BY fg.film_id, g.id
    """;

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

    @Override
    public Map<Long, Collection<Genre>> getGenresByFilmsIds(Collection<Long> filmsIds) {
        if (filmsIds == null || filmsIds.isEmpty()) {
            return Map.of();
        }

        String placeholders = filmsIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String query = String.format(GET_GENRES_BY_FILMS_IDS_QUERY, placeholders);
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
                    Map<Long, Collection<Genre>> result = new HashMap<>();

                    while (rs.next()) {
                        Long filmId = rs.getLong("film_id");
                        Genre genre = new Genre();
                        genre.setId(rs.getLong("genre_id"));
                        genre.setName(rs.getString("genre_name"));
                        result.computeIfAbsent(filmId, key -> new ArrayList<>()).add(genre);
                    }

                    return result;
                }
        );
    }
}