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

@Service
@Slf4j
public class UserService {
    protected UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        if (userStorage.getUser(userId) != null) {
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
        List<User> friends = new ArrayList<>();
        if (!userStorage.getUser(userId).getFriends().isEmpty()) {
            for (Long id : userStorage.getUser(userId).getFriends()) {
                friends.add(userStorage.getUser(id));
            }
        }
        return friends;
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        log.info("Получен запрос на получение общих друзей у {}ID и {}ID", userId, friendId);
        List<User> users = new ArrayList<>();
        Set<Long> friendsUser = userStorage.getUser(userId).getFriends();
        friendsUser.retainAll(userStorage.getUser(friendId).getFriends());

        if (!friendsUser.isEmpty()) {
            for (Long id : friendsUser) {
                users.add(userStorage.getUser(id));
            }
        } else {
            log.info("Друзья не найдены.");
        }
        return users;
    }
}
