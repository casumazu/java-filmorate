package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Service
@Slf4j
public class UserService {
    protected UserDbStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId < 0 || friendId < 0) {
            throw new UserNotFoundException("ID пользователей отрицательные");
        }
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (userStorage.getUser(userId) != null && userStorage.getUser(friendId) != null) {
            userStorage.removeFriend(userId, friendId);
        }
    }

    public List<User> getFriends(Long userId) {
        return userStorage.getFriends(userId);
    }

    public User deleteUser(Long id) {
        return userStorage.deleteUser(id);
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        return userStorage.getCommonFriends(userId, friendId);
    }
}