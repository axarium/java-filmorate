package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
public class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    void createAndGetUserById() {
        User newUser = new User();
        newUser.setName("Name");
        newUser.setLogin("Login");
        newUser.setEmail("Email@email.ru");
        newUser.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.createUser(newUser);

        Optional<User> userFromDb = userStorage.getUserById(createdUser.getId());

        assertThat(userFromDb)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("id", createdUser.getId());
                    assertThat(user).hasFieldOrPropertyWithValue("email", "Email@email.ru");
                    assertThat(user).hasFieldOrPropertyWithValue("login", "Login");
                    assertThat(user).hasFieldOrPropertyWithValue("name", "Name");
                    assertThat(user).hasFieldOrPropertyWithValue(
                            "birthday", LocalDate.of(1990, 1, 1)
                    );
                });
    }

    @Test
    void updateUser() {
        User newUser = new User();
        newUser.setName("Name");
        newUser.setLogin("Login");
        newUser.setEmail("Email@email.ru");
        newUser.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.createUser(newUser);

        createdUser.setEmail("NewEmail@email.ru");
        createdUser.setLogin("NewLogin");
        createdUser.setName("NewName");
        createdUser.setBirthday(LocalDate.of(1995, 5, 15));

        userStorage.updateUser(createdUser);

        Optional<User> updatedUser = userStorage.getUserById(createdUser.getId());

        assertThat(updatedUser)
                .isPresent()
                .hasValueSatisfying(userUpdated -> {
                    assertThat(userUpdated).hasFieldOrPropertyWithValue("id", createdUser.getId());
                    assertThat(userUpdated).hasFieldOrPropertyWithValue("email", "NewEmail@email.ru");
                    assertThat(userUpdated).hasFieldOrPropertyWithValue("login", "NewLogin");
                    assertThat(userUpdated).hasFieldOrPropertyWithValue("name", "NewName");
                    assertThat(userUpdated).hasFieldOrPropertyWithValue(
                            "birthday", LocalDate.of(1995, 5, 15)
                    );
                });
    }

    @Test
    void getAllUsers() {
        User firstUser = new User();
        firstUser.setName("Name");
        firstUser.setLogin("Login");
        firstUser.setEmail("Email@email.ru");
        firstUser.setBirthday(LocalDate.of(1990, 1, 1));

        User secondUser = new User();
        secondUser.setName("Name");
        secondUser.setLogin("Login");
        secondUser.setEmail("Email@email.com");
        secondUser.setBirthday(LocalDate.of(1990, 1, 1));

        userStorage.createUser(firstUser);
        userStorage.createUser(secondUser);

        Collection<User> users = userStorage.getAllUsers();

        assertThat(users)
                .isNotNull()
                .hasSize(2)
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder("Email@email.ru", "Email@email.com");
    }
}