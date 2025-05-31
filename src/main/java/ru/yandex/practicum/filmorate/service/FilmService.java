package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film addFilm(Film film) {
        log.debug("Попытка добавить фильм: {}", film);
        validate(film);
        return filmStorage.save(film);
    }

    public Film updateFilm(Film film) {
        log.debug("Попытка обновить фильм {}", film);
        if (film.getId() == 0 || film.getId() == null) {
            log.warn("Поле id не задано", film.getId());
            throw new NotFoundException("Поле id обязательно при обновлении фильма.");
        }
        validate(film);
        Film oldFilm = filmStorage.update(film);
        if (validate(oldFilm)) {
            oldFilm.setDuration(film.getDuration());
            oldFilm.setName(film.getName());
            oldFilm.setDescription(film.getDescription());
            oldFilm.setReleaseDate(film.getReleaseDate());
            log.debug("Фильм с id={} успешно обновлён", film.getId());
        }
        return oldFilm;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addlike(Long filmId, Long userId) {
        log.debug("Попытка добавить лайк на фильм с id={} от пользователя с id={} ", filmId, userId);
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> {
                    log.warn("Фильм с id={} не найден", filmId);
                    return new NotFoundException("Фильм с id= " + filmId + " не найден");
                });
        userService.getUserOrThrow(userId);
        log.debug("Найден пользователь с id {} метод addlike ", userId);

        film.getLikes().add(userId);
        log.debug("Лайк успешно добавлен: filmId={}, userId={}", filmId, userId);
        log.debug("Лайк успешно добавлен: filmId={}, userId={}", filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        log.debug("Попытка удалить лайк на фильм с id={} от пользователя с id={} ", filmId, userId);
        Film film = filmStorage.findFilmById(filmId).orElseThrow(() -> new NotFoundException("Фильм с id=" + filmId + " не найден"));

        userService.getUserOrThrow(userId);
        log.debug("Найден пользователь с id {} метод removeLike ", userId);

        if (!film.getLikes().remove(userId)) {
            log.warn("Удаление лайка не удалось — у пользователя с id={} нет лайка на фильме с id={}", userId, filmId);
            throw new NotFoundException("У пользователя с id=" + userId + " нет лайка на фильме с id=" + filmId);
        }
    }

    public List<Film> getTopFilms(int count) {
        log.debug("Получение топ-{} фильмов по количеству лайков", count);
        List<Film> topFilms = filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> Integer.compare(
                        f2.getLikes().size(),
                        f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
        log.debug("Получено топ-{} фильмов по количеству лайков", topFilms.size());
        return topFilms;
    }

    private boolean validate(Film film) {
        log.debug("Попытка валидация фильма с id {}", film.getId());
        LocalDate startDateRelease = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(startDateRelease))
            throw new ValidationException("Дата релиза должна быть не ранее 28.12.1895");
        return true;
    }
}
