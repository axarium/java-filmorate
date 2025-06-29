package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film_genre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.user_film_like.UserFilmLikeStorage;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final UserFilmLikeStorage userFilmLikeStorage;
    private final UserService userService;
    private final GenreService genreService;
    private final MpaService mpaService;

    public Collection<Film> getAllFilms() {
        Collection<Film> films = filmStorage.getAllFilms();

        addMpaInFilms(films);
        addLikesInFilms(films);
        addGenresInFilms(films);

        return films;
    }

    public Film getFilmById(Long id) {
        Film film = filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм не найден."));

        addMpaInFilm(film);
        addLikesInFilm(film);
        addGenresInFilm(film);

        return film;
    }

    public Film createFilm(Film film) {
        checkMpaInFilm(film);
        checkGenresInFilm(film);

        film = filmStorage.createFilm(film);

        addFilmGenres(film);
        addMpaInFilm(film);
        addGenresInFilm(film);

        return film;
    }

    public Film updateFilm(Film film) {
        addLikesInFilm(film);
        addMpaInFilm(film);
        addGenresInFilm(film);

        return filmStorage.updateFilm(film);
    }

    public Film addLike(Long id, Long userId) {
        Film film = filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм не найден."));
        User user = userService.getUserById(userId);

        userFilmLikeStorage.addLike(user.getId(), film.getId());
        addLikesInFilm(film);
        addMpaInFilm(film);
        addGenresInFilm(film);

        return film;
    }

    public void deleteLike(Long id, Long userId) {
        Film film = filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм не найден."));
        User user = userService.getUserById(userId);

        userFilmLikeStorage.deleteLike(user.getId(), film.getId());
    }

    public Collection<Film> getMostPopularFilms(Integer count) {
        return getAllFilms()
                .stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getUsersIdsWhoLikes().size()).reversed())
                .limit(count)
                .toList();
    }

    private void addLikesInFilm(Film film) {
        film.getUsersIdsWhoLikes().addAll(userFilmLikeStorage.getLikesByFilmId(film.getId()));
    }

    private void addLikesInFilms(Collection<Film> films) {
        Collection<Long> filmsIds = films.stream().map(Film::getId).collect(Collectors.toList());
        Map<Long, Set<Long>> filmsLikesMap = userFilmLikeStorage.getLikesByFilmsIds(filmsIds);

        for (Film film : films) {
            Set<Long> usersWhoLikes = filmsLikesMap.getOrDefault(film.getId(), new HashSet<>());
            film.getUsersIdsWhoLikes().clear();
            film.getUsersIdsWhoLikes().addAll(usersWhoLikes);
        }
    }

    private void addGenresInFilm(Film film) {
        Collection<FilmGenre> filmGenres = filmGenreStorage.getFilmGenresByFilmId(film.getId());

        film.getGenres().clear();

        for (FilmGenre filmGenre : filmGenres) {
            Genre genre = genreService.getGenreById(filmGenre.getGenreId());
            film.getGenres().add(genre);
        }
    }

    private void addGenresInFilms(Collection<Film> films) {
        Map<Long, Collection<Genre>> genresMap = filmGenreStorage.getGenresByFilmsIds(
                films.stream().map(Film::getId).toList()
        );

        for (Film film : films) {
            film.getGenres().clear();
            Collection<Genre> genres = genresMap.getOrDefault(film.getId(), List.of());
            film.getGenres().addAll(genres);
        }
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

    private void checkMpaInFilm(Film film) {
        try {
            if (film.getMpa() != null) {
                mpaService.getMpaById(film.getMpa().getId());
            }
        } catch (NotFoundException exception) {
            throw new NotFoundException("Рейтинг фильма не найден.");
        }
    }

    private void checkGenresInFilm(Film film) {
        try {
            Collection<Genre> genres = film.getGenres();

            for (Genre genre : genres) {
                genreService.getGenreById(genre.getId());
            }
        } catch (NotFoundException exception) {
            throw new NotFoundException("Жанр фильма не найден.");
        }
    }

    private void addFilmGenres(Film film) {
        Collection<Genre> genres = film.getGenres();

        for (Genre genre : genres) {
            filmGenreStorage.addFilmGenre(film.getId(), genre.getId());
        }
    }
}