package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
public class FilmNotFoundException extends IllegalArgumentException{
    public FilmNotFoundException(String message) {
        super(message);
        log.error(message);
    }

    public FilmNotFoundException(HttpStatus status,String message) {
        super(message);
        log.error(message, status);
    }

    @ExceptionHandler(FilmNotFoundException.class)
    public ResponseEntity<String> handleFilmNotFoundException(FilmNotFoundException ex) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
