package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    public void save(User user);

    public User update(User user);

    public Collection<User> getAllUsers();

    public Optional<User> findById(Long id);

    public void deleteById(Long id);

}
