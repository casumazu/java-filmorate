package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
public class MpaNotFoundException extends IllegalArgumentException {

    public MpaNotFoundException(String message) {
        super(message);
        log.error(message);
    }

    public MpaNotFoundException(HttpStatus status, String message) {
        super(message);
        log.error(message, status);
    }

    @ExceptionHandler(MpaNotFoundException.class)
    public ResponseEntity<String> handleMpaNotFoundException(MpaNotFoundException ex) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
