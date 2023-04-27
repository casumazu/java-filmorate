package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    private Integer id = 0;

    @GetMapping
    public List<User> getUsers() {
        log.info("Получен GET-запрос на получение данных всех пользователей");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
            if (isValidUser(user)) {
                user.setId(id++);
                users.put(user.getId(), user);
                log.trace("Пользователь добавлен: {}.", user);
            }
            log.trace("Количество пользователей: {}.", users.size());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Получен PUT-запрос к эндпоинту: '/users' на обновление пользователя с ID={}", user.getId());
        try {
            if (isValidUser(user) ||user.getId() == null) {
                user.setId(id + 1);
            }
            if (isValidUser(user)) {
                users.put(user.getId(), user);
                id++;
            }
        } catch (ValidationException e) {
            throw new ValidationException("Ошибка валидации");
        }
        return user;
    }

    private boolean isValidUser(User user) {
        if (!user.getEmail().contains("@") || user.getEmail() == null) {
            throw new ValidationException("Некорректный e-mail: " + user.getEmail());
        }
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().indexOf(' ') >= 0) {
            throw new ValidationException("Некорректный логин: " + user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Некорректная дата рождения: " + user.getBirthday());
        }
        if (user.getName().isEmpty() || user.getName() == null) {
            user.setName(user.getLogin());
        }
        return true;
    }
}
