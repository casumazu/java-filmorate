package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@Slf4j
public class GenreService {
    GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getGenreByID(Integer id) {
        log.info("Получен запрос на получение жанра по {}ID", id);
        if (id <= 0) {
            throw new ValidationException("ID жанра меньше или ровно 0");
        }
        return genreStorage.getGenreByID(id);
    }

    public List<Genre> getGenre() {
        log.info("Получен запрос на получение всех жанров");
        return genreStorage.getGenre();
    }
}