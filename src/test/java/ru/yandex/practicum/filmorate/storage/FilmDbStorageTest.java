package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class})
public class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;

    @Test
    void createAndGetFilmById() {
        Film newFilm = new Film();
        newFilm.setName("Name");
        newFilm.setDescription("Description");
        newFilm.setReleaseDate(LocalDate.of(2010, 7, 16));
        newFilm.setDuration(60);
        newFilm.setMpa(new Mpa());
        newFilm.getMpa().setId(1L);

        Film createdFilm = filmDbStorage.createFilm(newFilm);

        Optional<Film> filmFromDb = filmDbStorage.getFilmById(createdFilm.getId());

        assertThat(filmFromDb)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("id", createdFilm.getId());
                    assertThat(film).hasFieldOrPropertyWithValue("name", "Name");
                    assertThat(film).hasFieldOrPropertyWithValue("description", "Description");
                    assertThat(film).hasFieldOrPropertyWithValue(
                            "releaseDate", LocalDate.of(2010, 7, 16)
                    );
                    assertThat(film).hasFieldOrPropertyWithValue("duration", 60);
                    assertThat(film.getMpa().getId()).isEqualTo(1L);
                });
    }

    @Test
    void updateFilm() {
        Film newFilm = new Film();
        newFilm.setName("Name");
        newFilm.setDescription("Description");
        newFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        newFilm.setDuration(90);
        newFilm.setMpa(new Mpa());
        newFilm.getMpa().setId(1L);

        Film createdFilm = filmDbStorage.createFilm(newFilm);

        createdFilm.setName("NewName");
        createdFilm.setDescription("NewDescription");
        createdFilm.setReleaseDate(LocalDate.of(2020, 12, 31));
        createdFilm.setDuration(120);
        createdFilm.getMpa().setId(2L);

        filmDbStorage.updateFilm(createdFilm);

        Optional<Film> filmFromDb = filmDbStorage.getFilmById(createdFilm.getId());

        assertThat(filmFromDb)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("id", createdFilm.getId());
                    assertThat(film).hasFieldOrPropertyWithValue("name", "NewName");
                    assertThat(film).hasFieldOrPropertyWithValue("description", "NewDescription");
                    assertThat(film).hasFieldOrPropertyWithValue(
                            "releaseDate", LocalDate.of(2020, 12, 31)
                    );
                    assertThat(film).hasFieldOrPropertyWithValue("duration", 120);
                    assertThat(film.getMpa().getId()).isEqualTo(2L);
                });
    }

    @Test
    void getAllFilms() {
        Film firstFilm = new Film();
        firstFilm.setName("Name");
        firstFilm.setDescription("Description");
        firstFilm.setReleaseDate(LocalDate.of(2001, 1, 1));
        firstFilm.setDuration(100);
        firstFilm.setMpa(new Mpa());
        firstFilm.getMpa().setId(1L);

        Film firstCreatedFilm = filmDbStorage.createFilm(firstFilm);

        Film secondFilm = new Film();
        secondFilm.setName("Name");
        secondFilm.setDescription("Description");
        secondFilm.setReleaseDate(LocalDate.of(2002, 2, 2));
        secondFilm.setDuration(120);
        secondFilm.setMpa(new Mpa());
        secondFilm.getMpa().setId(1L);

        Film secondCreatedFilm = filmDbStorage.createFilm(secondFilm);

        Collection<Film> films = filmDbStorage.getAllFilms();

        assertThat(films)
                .isNotNull()
                .hasSize(2)
                .anySatisfy(film -> assertThat(film)
                        .hasFieldOrPropertyWithValue("id", firstCreatedFilm.getId())
                        .hasFieldOrPropertyWithValue("name", "Name"))
                .anySatisfy(film -> assertThat(film)
                        .hasFieldOrPropertyWithValue("id", secondCreatedFilm.getId())
                        .hasFieldOrPropertyWithValue("name", "Name"));
    }

}