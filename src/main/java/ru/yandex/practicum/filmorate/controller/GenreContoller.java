package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/genres")
public class GenreContoller {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreContoller(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @GetMapping(value = "/{id}")
    public Genre getGenreByID(@PathVariable Integer id) {
        log.info("Получен GET-запрос на получение жанра по ID");
        return genreStorage.getGenreByID(id);
    }

    @GetMapping
    public List<Genre> getGenres() {
        log.info("Получен запрос на получение фильма по ID");
        return genreStorage.getGenre();
    }
}
