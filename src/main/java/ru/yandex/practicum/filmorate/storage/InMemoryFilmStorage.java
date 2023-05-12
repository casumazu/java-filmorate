package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films;
    private Long id;

    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
        id = 0L;
    }

    @Override
    public List<Film> getFilms() {
        log.trace("Возвращены все фильмы.");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        log.info("Получен POST-запрос к эндпоинду -> /films на добавление фильм с ID{}", id + 1);
        if (isValid(film)) {
            film.setId(++id);
            films.put(film.getId(), film);
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Получен PUT-запрос к эндпоинду -> /films на изменения фильма с ID{}", film.getId());
        try {
            if (!films.containsKey(film.getId())) {
                throw new FilmNotFoundException("Такого фильма нет");
            }
            if (isValid(film)) {
                films.put(film.getId(), film);
                log.trace("Фильм успешно обновлён: {}.", film);
            }
        } catch (ValidationException e) {
            log.trace("Не удалось обновить фильм: {}.", e.getMessage());
            throw new ValidationException("Ошибка валидации");
        } catch (UserNotFoundException e) {
            throw new FilmNotFoundException("Такого фильма не существует");
        }
        return film;
    }

    @Override
    public Film getFilmById(Long filmId) {
        log.info("Получен GET-запрос на получение фильма по ID {}", filmId);
        if (!films.containsKey(filmId)) {
            log.trace("Не удалось получить фильм по ID фильм: {}.", filmId);
            throw new FilmNotFoundException(HttpStatus.BAD_REQUEST, "Фильма с таким ID не существует");
        }
        return films.get(filmId);
    }

    public boolean isValid(Film film) {
        if (film.getName() == null || film.getName().isBlank() || film.getName().isEmpty()) {
            throw new ValidationException("Название фильма не должно быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма должно быть пустым или не больше 200 символов: " + film.getDescription().length());
        }
        if ((film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)))) {
            throw new ValidationException("Некорректная дата выхода фильма: " + film.getReleaseDate());
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительной: " + film.getDuration());
        }
        return true;
    }
}
