package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
public class FilmService {
    protected FilmStorage filmStorage;
    protected UserStorage userStorage;
    protected LikesStorage likesStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       LikesStorage likesStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likesStorage = likesStorage;
    }

    public void addLikeFilm(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUser(userId);
        if (film == null) {
            log.info("Получен запрос на несуществующий фильм");
            throw new FilmNotFoundException("Фильма не найден");
        }
        if (user == null) {
            log.info("Получен запрос на несуществующего пользователя");
            throw new UserNotFoundException("Пользователь не найден");
        }
        likesStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUser(userId);
        if (film == null) {
            log.info("Получен запрос на несуществующий фильм");
            throw new FilmNotFoundException(HttpStatus.NOT_FOUND, "Фильма не найден");
        }
        if (user == null) {
            log.info("Получен запрос на несуществующего пользователя");
            throw new UserNotFoundException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
        if (likesStorage.getLikes(filmId).contains(userId)) {
            likesStorage.getLikes(filmId).remove(user.getId());
        } else {
            log.info("Лайк от пользователя не найден");
            throw new FilmNotFoundException("Пользователь не ставил лайк данному фильму");
        }
    }

    public List<Film> getPopular(Integer count) {
        if (count < 1) {
            throw new ValidationException("Количество фильмов не должно быть меньше 1");
        }
        return likesStorage.getPopular(count);
    }
}