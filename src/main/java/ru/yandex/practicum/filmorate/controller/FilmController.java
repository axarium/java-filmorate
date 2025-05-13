package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private long filmsCount = 0;

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Process GET /films request");

        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Process POST /films request with film {}", film);

        filmsCount = IdGenerator.generateId(filmsCount);
        film.setId(filmsCount);
        films.put(film.getId(), film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info("Process PUT /films request with film {}", film);

        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм не найден.");
        }

        films.put(film.getId(), film);

        return film;
    }
}