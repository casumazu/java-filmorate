package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> usersStorage;
    private Long id;

    public InMemoryUserStorage (){
        usersStorage = new HashMap<>();
        id = 0L;
    }
    @Override
    public List<User> getUsers() {
        log.trace("Получен GET-запрос на получение данных всех пользователей");
        return new ArrayList<>(usersStorage.values());
    }

    @Override
    public User create(User user) {
        log.trace("Получен POST-запрос к эндпоинду -> /users на добавление пользователя с ID{}", id + 1);
        if (usersStorage.containsKey(user.getId())) {
            log.trace("Пользователь уже существует:{}.", user);
            throw new ValidationException("Данный пользователь уже существует");
        }
        if (isValid(user)) {
            user.setId(++id);
            usersStorage.put(user.getId(), user);
            log.trace("Пользователь добавлен: {}.", user);
        }
        return user;
    }

    @Override
    public User update(User user) {

        log.trace("Получен PUT-запрос к эндпоинту: '/users' на обновление пользователя с ID={}", user.getId());
        try {
            if (!usersStorage.containsKey(user.getId())) {
                throw new UserNotFoundException("Пользователь не существует");
            }
            if (isValid(user)) {
                usersStorage.put(user.getId(), user);
            }
        } catch (ValidationException e) {
            throw new ValidationException("Ошибка валидации");
        } catch (UserNotFoundException e){
            throw new UserNotFoundException("Пользователь не существует");
        }
        return user;
    }

    @Override
    public User getUser(Long userId) {
        if(!usersStorage.containsKey(userId)){
            log.info("Пользователя с ID {} не существует", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        return usersStorage.get(userId);
    }

    private boolean isValid(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный e-mail: " + user.getEmail());
        }
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Некорректный логин: " + user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Некорректная дата рождения: " + user.getBirthday());
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return true;
    }
}
