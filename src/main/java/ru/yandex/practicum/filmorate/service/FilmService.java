package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    protected FilmStorage filmStorage;
    protected UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLikeFilm(Long filmId, Long userId) {
        if (filmStorage.getFilmById(filmId) == null) {
            log.info("Получен запрос на несуществующий фильм");
            throw new FilmNotFoundException("Фильма не найден");
        }
        if (userStorage.getUser(userId) == null) {
            log.info("Получен запрос на несуществующего пользователя");
            throw new UserNotFoundException("Пользователь не найден");
        }
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().add(userStorage.getUser(userId).getId());
    }

    public void deleteLike(Long filmId, Long userId) {
        if (filmStorage.getFilmById(filmId) == null) {
            log.info("Получен запрос на несуществующий фильм");
            throw new FilmNotFoundException(HttpStatus.NOT_FOUND, "Фильма не найден");

        }
        if (userStorage.getUser(userId) == null) {
            log.info("Получен запрос на несуществующего пользователя");
            throw new UserNotFoundException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
        Film film = filmStorage.getFilmById(filmId);
        if (film.getLikes().contains(userId)) {
            film.getLikes().remove(userStorage.getUser(userId).getId());
        } else {
            log.info("Лайк от пользователя не найден");
            throw new FilmNotFoundException("Пользователь не ставил лайк данному фильму");
        }
    }

    public List<Film> getPopular(Integer count) {
        if (count < 1) {
            throw new ValidationException("Количество фильмов не должно быть меньше 1");
        }
        return filmStorage.getFilms().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }


}
