package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    public Collection<User> getAllUsers();

    public Optional<User> getUserById(Long id);

    public User createUser(User user);

    public User updateUser(User user);
}