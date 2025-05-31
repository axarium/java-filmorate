package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
public class User {

    private Long id;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Некорректный формат электронной почты")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы")
    private String login;

    String name;

    @NotNull(message = "Дата рождения должна быть указана")
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();
}