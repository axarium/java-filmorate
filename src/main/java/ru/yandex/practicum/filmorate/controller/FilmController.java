package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        Film created = filmService.addFilm(film);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody @Valid Film film) {
        Film updatedFilm = filmService.updateFilm(film);
        return ResponseEntity.ok(updatedFilm);

    }

    @GetMapping
    public ResponseEntity<Collection<Film>> getAllFilms() {
        Collection<Film> films = filmService.getAllFilms();
        return ResponseEntity.ok(films);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("Вызов метода addLike() с параметрами:filmId={},userId={}", id, userId);
        filmService.addlike(id, userId);
        log.debug("Пользователь {} поставил лайк фильму {}", id, userId);

    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("Вызов метода removeLike() c параметрами:filmId={},userId={}", id, userId);
        filmService.removeLike(id, userId);
        log.debug("Пользователь {} удалил лайк у фильма {}", id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.debug("Вызов метода getPopularFilms() c параметром count={}", count);
        List<Film> popularFilms = filmService.getTopFilms(count);
        log.debug("Вовзращено {} популярных фильмов", count);
        return popularFilms;

    }

}
