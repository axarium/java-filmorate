package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private static final String REGEXP_FOR_LOGIN = "\\S+";

    private Long id;
    @Email(message = "Email не соответствует формату.")
    private String email;
    @NotNull(message = "Логин является обязательным полем.")
    @Pattern(regexp = REGEXP_FOR_LOGIN, message = "Логин не должен быть пустым и не должен содержать пробелы.")
    private String login;
    private String name;
    @PastOrPresent(message = "День рождения не может быть в будущем.")
    private LocalDate birthday;
}