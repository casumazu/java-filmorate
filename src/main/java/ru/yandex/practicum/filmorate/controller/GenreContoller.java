package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/genres")
public class GenreContoller {
    private final GenreService genreService;

    @Autowired
    public GenreContoller(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping(value = "/{id}")
    public Genre getGenreByID(@PathVariable Integer id) {
        return genreService.getGenreByID(id);
    }

    @GetMapping
    public List<Genre> getGenres() {
        return genreService.getGenre();
    }
}