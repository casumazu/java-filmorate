package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
public class GenreNotFoundException extends IllegalArgumentException {
    public GenreNotFoundException(String message) {
        super(message);
        log.error(message);
    }

    public GenreNotFoundException(HttpStatus status, String message) {
        super(message);
        log.error(message, status);
    }

    @ExceptionHandler(GenreNotFoundException.class)
    public ResponseEntity<String> handleGenreNotFoundException(GenreNotFoundException ex) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
