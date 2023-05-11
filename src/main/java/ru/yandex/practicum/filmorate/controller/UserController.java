package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    UserStorage userStorage;
    UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userStorage.update(user);
    }

    @GetMapping(value = "/{id}")
    public User getUser(@PathVariable Long id){
        return userStorage.getUser(id);
    }


    // Добавление в друзья
    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId){
        userService.addFriend(id,friendId);
    }

    // Удаление из друзей
    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id,@PathVariable Long friendId){
        userService.removeFriend(id,friendId);
    }

    // Найти общих друзей
    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId){
        return userService.getCommonFriends(id, otherId);
    }

    // Показать друзей
    @GetMapping(value = "/{id}/friends")
    public List<User> getFriends(@PathVariable Long id){
        return userService.getFriends(id);
    }
}
