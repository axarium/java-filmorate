package ru.yandex.practicum.filmorate.storage.user_film_like;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface UserFilmLikeStorage {

    Collection<Long> getLikesByFilmId(Long filmId);

    Map<Long, Set<Long>> getLikesByFilmsIds(Collection<Long> filmsIds);

    void addLike(Long userId, Long filmId);

    void deleteLike(Long userId, Long filmId);
}