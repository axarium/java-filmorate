package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.FilmDate;
import ru.yandex.practicum.filmorate.validator.OnCreate;
import ru.yandex.practicum.filmorate.validator.OnUpdate;

import java.time.LocalDate;

@Data
public class Film {
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    private Long id;
    @NotBlank(message = "Название не может быть пустым.", groups = {OnCreate.class, OnUpdate.class})
    private String name;
    @Size(
            max = MAX_DESCRIPTION_LENGTH,
            message = "Длина описания не может быть больше " + MAX_DESCRIPTION_LENGTH + " символов.",
            groups = {OnCreate.class, OnUpdate.class}
    )
    private String description;
    @FilmDate(
            message = "Дата фильма должна быть не раньше 28 декабря 1895 года.",
            groups = {OnCreate.class, OnUpdate.class}
    )
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность должна быть положительной.", groups = {OnCreate.class, OnUpdate.class})
    private Integer duration;
}