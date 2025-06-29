package ru.yandex.practicum.filmorate.storage.film_genre;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.Collection;

public interface FilmGenreStorage {

    Collection<FilmGenre> getFilmGenresByFilmId(Long filmId);

    void addFilmGenre(Long filmId, Long genreId);
}