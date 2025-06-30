package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> getAllFilms();

    Optional<Film> getFilmById(Long id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void addLike(Long userId, Long filmId);

    void deleteLike(Long userId, Long filmId);
}