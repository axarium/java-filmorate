package ru.yandex.practicum.filmorate.storage.user_film_like;

import java.util.Collection;

public interface UserFilmLikeStorage {

    Collection<Long> getUsersIdsByFilmId(Long filmId);

    void addLike(Long userId, Long filmId);

    void deleteLike(Long userId, Long filmId);
}