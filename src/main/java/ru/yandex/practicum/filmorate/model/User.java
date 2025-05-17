package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.OnCreate;
import ru.yandex.practicum.filmorate.validator.OnUpdate;

import java.time.LocalDate;

@Data
public class User {
    private static final String REGEXP_FOR_LOGIN = "\\S+";

    private Long id;
    @NotBlank(message = "Email не может быть пустым.", groups = {OnCreate.class, OnUpdate.class})
    @Email(message = "Email не соответствует формату.", groups = {OnCreate.class, OnUpdate.class})
    private String email;
    @NotNull(message = "Логин является обязательным полем.", groups = {OnCreate.class, OnUpdate.class})
    @Pattern(
            regexp = REGEXP_FOR_LOGIN,
            message = "Логин не должен быть пустым и не должен содержать пробелы.",
            groups = {OnCreate.class, OnUpdate.class}
    )
    private String login;
    private String name;
    @PastOrPresent(message = "День рождения не может быть в будущем.", groups = {OnCreate.class, OnUpdate.class})
    private LocalDate birthday;
}