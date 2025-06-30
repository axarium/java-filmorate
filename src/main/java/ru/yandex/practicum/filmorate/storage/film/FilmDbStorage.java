package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private final MpaService mpaService;

    private static final String GET_ALL_QUERY = "SELECT * FROM film";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM film WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO film (name, description, release_date, duration, "
            + "mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE film SET name = ?, description = ?, release_date = ?"
            + ", duration = ?, mpa_id = ? WHERE id = ?";

    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String GET_GENRES_BY_FILM_ID_QUERY = """
                SELECT g.id, g.name
                FROM genre g
                JOIN film_genre fg ON g.id = fg.genre_id
                WHERE fg.film_id = ?
            """;
    private static final String GET_GENRES_BY_FILMS_IDS_QUERY = """
                SELECT fg.film_id, g.id AS genre_id, g.name AS genre_name
                FROM film_genre fg
                JOIN genre g ON fg.genre_id = g.id
                WHERE fg.film_id IN (%s)
                ORDER BY fg.film_id, g.id
            """;

    private static final String GET_LIKES_BY_FILM_ID_QUERY = "SELECT user_id FROM user_film_like WHERE film_id = ?";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO user_film_like (user_id, film_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM user_film_like WHERE user_id = ? AND film_id = ?";
    private static final String GET_LIKES_BY_FILMS_IDS_QUERY =
            "SELECT film_id, user_id FROM user_film_like WHERE film_id IN (%s)";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, MpaService mpaService) {
        super(jdbc, mapper);
        this.mpaService = mpaService;
    }

    @Override
    public Collection<Film> getAllFilms() {
        Collection<Film> films = getMany(GET_ALL_QUERY);
        addGenresInFilms(films);
        addLikesInFilms(films);
        addMpaInFilms(films);

        return films;
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        Optional<Film> film = getOne(GET_BY_ID_QUERY, id);

        film.ifPresent(f -> {
            addGenresInFilm(f);
            addLikesInFilm(f);
            addMpaInFilm(f);
        });

        return film;
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
        addFilmGenres(film);
        addGenresInFilm(film);
        addMpaInFilm(film);

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
        addGenresInFilm(film);
        addLikesInFilm(film);
        addMpaInFilm(film);

        return film;
    }

    @Override
    public void addLike(Long userId, Long filmId) {
        insert(INSERT_LIKE_QUERY, userId, filmId);
    }

    @Override
    public void deleteLike(Long userId, Long filmId) {
        delete(DELETE_LIKE_QUERY, userId, filmId);
    }


    private void addFilmGenres(Film film) {
        Collection<Genre> genres = film.getGenres();

        for (Genre genre : genres) {
            update(INSERT_FILM_GENRE_QUERY, film.getId(), genre.getId());
        }
    }

    private void addGenresInFilm(Film film) {
        Collection<Genre> genres = jdbc.query(
                GET_GENRES_BY_FILM_ID_QUERY,
                (rs, rowNum) -> {
                    Genre genre = new Genre();
                    genre.setId(rs.getLong("id"));
                    genre.setName(rs.getString("name"));
                    return genre;
                },
                film.getId()
        );

        film.getGenres().clear();
        film.getGenres().addAll(genres);
    }

    private void addGenresInFilms(Collection<Film> films) {
        if (films == null || films.isEmpty()) {
            return;
        }

        Map<Long, Film> filmsMap = new HashMap<>();

        for (Film film : films) {
            film.getGenres().clear();
            filmsMap.put(film.getId(), film);
        }

        queryWithInCondition(
                GET_GENRES_BY_FILMS_IDS_QUERY,
                filmsMap.keySet(),
                rs -> {
                    Long filmId = rs.getLong("film_id");
                    Genre genre = new Genre();

                    genre.setId(rs.getLong("genre_id"));
                    genre.setName(rs.getString("genre_name"));

                    filmsMap.get(filmId).getGenres().add(genre);
                }
        );
    }

    private void addLikesInFilm(Film film) {
        List<Long> usersIds = getSimpleList(GET_LIKES_BY_FILM_ID_QUERY, Long.class, film.getId());

        film.getUsersIdsWhoLikes().clear();
        film.getUsersIdsWhoLikes().addAll(usersIds);
    }

    private void addLikesInFilms(Collection<Film> films) {
        if (films == null || films.isEmpty()) {
            return;
        }

        Map<Long, Film> filmsMap = new HashMap<>();

        for (Film film : films) {
            film.getUsersIdsWhoLikes().clear();
            filmsMap.put(film.getId(), film);
        }

        queryWithInCondition(
                GET_LIKES_BY_FILMS_IDS_QUERY,
                filmsMap.keySet(),
                rs -> {
                    Long filmId = rs.getLong("film_id");
                    Long userId = rs.getLong("user_id");
                    Film film = filmsMap.get(filmId);

                    if (film != null) {
                        film.getUsersIdsWhoLikes().add(userId);
                    }
                }
        );
    }

    private void addMpaInFilm(Film film) {
        if (film.getMpa() != null) {
            film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
        }
    }

    private void addMpaInFilms(Collection<Film> films) {
        Collection<Mpa> allMpa = mpaService.getAllMpa();
        Map<Long, Mpa> mpaMap = allMpa.stream().collect(Collectors.toMap(Mpa::getId, mpa -> mpa));

        for (Film film : films) {
            Mpa mpa = mpaMap.get(film.getMpa().getId());

            if (mpa != null) {
                film.setMpa(mpa);
            }
        }
    }
}