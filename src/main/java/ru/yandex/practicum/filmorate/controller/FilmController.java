package ru.yandex.practicum.filmorate.controller;

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
    public List<Film> returnFilms() {
        log.trace("Возвращены все фильмы.");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        log.info("Получен POST-запрос к эндпоинду -> /films на добавление фильм с ID{}", id + 1);
        if (isValid(film)) {
            film.setId(++id);
            films.put(film.getId(), film);
        }
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        log.info("Получен PUT-запрос к эндпоинту -> /films на обновление фильма с ID={}", film.getId());
        if (film.getId() == null) {
            film.setId(id + 1);
        }
        if (isValid(film)) {
            films.put(film.getId(), film);
            id++;
        }
        return film;
    }

    private boolean isValid(Film film) {
        if (film.getName().isEmpty()) {
            throw new ValidationException("Название фильма не должно быть пустым!");
        }
        if ((film.getDescription().length()) > 200 || (film.getDescription().isEmpty())) {
            throw new ValidationException("Описание фильма больше 200 символов или пустое: " + film.getDescription().length());
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Некорректная дата релиза фильма: " + film.getReleaseDate());
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительной: " + film.getDuration());
        }
        return true;
    }
}
