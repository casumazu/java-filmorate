package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private Map<Integer, Film> films;
    private Integer id;

    public FilmController() {
        id = 0;
        films = new HashMap<>();
    }

    @GetMapping
    public List<Film> getFilms() {
        log.trace("Возвращены все фильмы.");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен POST-запрос к эндпоинду -> /films на добавление фильм с ID{}", id + 1);
        if (isValid(film)) {
            film.setId(++id);
            films.put(film.getId(), film);
        }
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        try {
            if (!films.containsKey(film.getId())) {
                throw new ValidationException("Такого фильма нет");
            }
            if (isValid(film)) {
                films.put(film.getId(), film);
                log.trace("Фильм успешно обновлён: {}.", film);
            }
        } catch (ValidationException e) {
            log.trace("Не удалось обновить фильм: {}.", e.getMessage());
            throw new RuntimeException("Ошибка валидации: " + e.getMessage(), e);
        } finally {
            log.trace("Количество фильмов: {}.", films.size());
        }
        return film;
    }

    public boolean isValid(@NonNull Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не должно быть пустым!");
        }
        if (film.getDescription().length() > 200 || (film.getDescription().isEmpty())) {
            throw new ValidationException("Описание фильма должно быть пустым и не больше 200 символов: "
                    + film.getDescription().length());
        }
        if ((film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)))) {
            throw new ValidationException("Некорректная дата релиза фильма: " + film.getReleaseDate());
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительной: " + film.getDuration());
        }
        return true;
    }
}
