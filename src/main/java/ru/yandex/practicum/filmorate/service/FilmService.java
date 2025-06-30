package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final GenreService genreService;
    private final MpaService mpaService;

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм не найден."));
    }

    public Film createFilm(Film film) {
        checkMpaInFilm(film);
        checkGenresInFilm(film);

        film = filmStorage.createFilm(film);

        return film;
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film addLike(Long id, Long userId) {
        Film film = filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм не найден."));
        User user = userService.getUserById(userId);

        filmStorage.addLike(user.getId(), film.getId());

        return film;
    }

    public void deleteLike(Long id, Long userId) {
        Film film = filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм не найден."));
        User user = userService.getUserById(userId);

        filmStorage.deleteLike(user.getId(), film.getId());
    }

    public Collection<Film> getMostPopularFilms(Integer count) {
        return getAllFilms()
                .stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getUsersIdsWhoLikes().size()).reversed())
                .limit(count)
                .toList();
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
}