package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRowMapper;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaDbStorage.class, MpaRowMapper.class})
public class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    void getMpaById() {
        Optional<Mpa> mpaFromDb = mpaDbStorage.getMpaById(1L);

        assertThat(mpaFromDb)
                .isPresent()
                .hasValueSatisfying(mpa -> {
                    assertThat(mpa.getId()).isEqualTo(1L);
                    assertThat(mpa.getName()).isNotBlank();
                });
    }

    @Test
    void getAllMpa() {
        Collection<Mpa> mpaList = mpaDbStorage.getAllMpa();

        assertThat(mpaList)
                .isNotNull()
                .isNotEmpty()
                .allSatisfy(mpa -> {
                    assertThat(mpa.getId()).isNotNull();
                    assertThat(mpa.getName()).isNotBlank();
                });
    }
}