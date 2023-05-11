package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    protected UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId < 0 || friendId < 0) {
            throw new UserNotFoundException("id пользователей отрицательные");
        }
        if (userStorage.getUser(userId) != null || userStorage.getUser(friendId) != null) {
            userStorage.getUser(userId).getFriends().add(friendId);
            userStorage.getUser(friendId).getFriends().add(userId);
        } else {
            throw new UserNotFoundException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }

    }

    public void removeFriend(Long userId, Long friendId) {
        userStorage.getUser(userId).getFriends().remove(friendId);
        userStorage.getUser(friendId).getFriends().remove(userId);
    }

    public List<User> getFriends(Long userId) {
        return userStorage.getUser(userId).getFriends().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        Set<Long> friends = userStorage.getUser(userId).getFriends();
        Set<Long> friendsOther = userStorage.getUser(friendId).getFriends();
        List<Long> common = friends.stream()
                .filter(friendsOther::contains)
                .collect(Collectors.toList());
        List<User> users = new ArrayList<>();
        for (Long friend : common) {
            users.add(userStorage.getUser(friend));
        }
        if (users.isEmpty()) {
            log.info("Общих друзей нет");
        }
        return users;
    }
}
