package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public List<User> getUsers();

    public User create(User user);

    public User update(User user);

    public User getUser(Long userId);
}
