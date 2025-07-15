package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
@Primary
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private static final String GET_ALL_QUERY = "SELECT * FROM app_user";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM app_user WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO app_user (email, login, name, birthday) "
            + "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE app_user SET email = ?, login = ?, name = ?, birthday = ? "
            + "WHERE id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> getAllUsers() {
        return getMany(GET_ALL_QUERY);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return getOne(GET_BY_ID_QUERY, id);
    }

    @Override
    public User createUser(User user) {
        Long id = insertWithGeneratedKey(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);

        return user;
    }

    @Override
    public User updateUser(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        return user;
    }
}