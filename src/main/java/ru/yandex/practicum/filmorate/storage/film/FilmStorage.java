package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    public Collection<Film> getAllFilms();

    public Optional<Film> getFilmById(Long id);

    public Film createFilm(Film film);

    public Film updateFilm(Film film);
}