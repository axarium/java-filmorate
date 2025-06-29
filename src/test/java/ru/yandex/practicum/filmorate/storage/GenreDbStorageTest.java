package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class, GenreRowMapper.class})
public class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    void getGenreById() {
        Optional<Genre> genreFromDb = genreDbStorage.getGenreById(1L);

        assertThat(genreFromDb)
                .isPresent()
                .hasValueSatisfying(genre -> {
                    assertThat(genre.getId()).isEqualTo(1L);
                    assertThat(genre.getName()).isNotBlank();
                });
    }

    @Test
    void getAllGenres() {
        Collection<Genre> genres = genreDbStorage.getAllGenres();

        assertThat(genres)
                .isNotNull()
                .isNotEmpty()
                .allSatisfy(genre -> {
                    assertThat(genre.getId()).isNotNull();
                    assertThat(genre.getName()).isNotBlank();
                });
    }
}

