package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Comparator;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм не найден."));
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        if (filmStorage.getFilmById(film.getId()).isEmpty()) {
            throw new NotFoundException("Фильм не найден.");
        }

        return filmStorage.updateFilm(film);
    }

    public Film addLike(Long id, Long userId) {
        Film film = filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм не найден."));
        User user = userService.getUserById(userId);
        film.getUsersIdsWhoLikes().add(user.getId());

        return film;
    }

    public void deleteLike(Long id, Long userId) {
        Film film = filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм не найден."));
        User user = userService.getUserById(userId);
        film.getUsersIdsWhoLikes().remove(user.getId());
    }

    public Collection<Film> getMostPopularFilms(Integer count) {
        return filmStorage.getAllFilms()
                .stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getUsersIdsWhoLikes().size()).reversed())
                .limit(count)
                .toList();
    }
}