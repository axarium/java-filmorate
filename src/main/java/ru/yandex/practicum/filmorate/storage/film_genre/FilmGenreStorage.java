package ru.yandex.practicum.filmorate.storage.film_genre;

import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Map;

public interface FilmGenreStorage {

    Collection<FilmGenre> getFilmGenresByFilmId(Long filmId);

    Map<Long, Collection<Genre>> getGenresByFilmsIds(Collection<Long> filmsIds);

    void addFilmGenre(Long filmId, Long genreId);
}