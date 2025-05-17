package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.FilmDate;

import java.time.LocalDate;

public class FilmDateValidator implements ConstraintValidator<FilmDate, LocalDate> {
    private static final LocalDate MIN_FILM_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.isAfter(MIN_FILM_DATE);
    }
}