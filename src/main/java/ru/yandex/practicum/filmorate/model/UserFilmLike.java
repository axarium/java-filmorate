package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class UserFilmLike {
    private Long userId;
    private Long filmId;
}