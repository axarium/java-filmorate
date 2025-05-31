package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    public Film save(Film film);

    public Film update(Film film);

    public Collection<Film> getAllFilms();

    public Optional<Film> findFilmById(Long id);

    public void deleteFilmById(Long id);
}
