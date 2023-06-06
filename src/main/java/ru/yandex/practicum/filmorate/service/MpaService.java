package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import javax.validation.ValidationException;
import java.util.List;

@Service
@Slf4j
public class MpaService {
    MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Mpa getMpaByID(Integer id) {
        log.info("Получен запрос на получение MPA по {}ID", id);
        if (id <= 0) {
            throw new ValidationException("ID MPA меньше или ровно 0");
        }
        return mpaStorage.getMpaByID(id);
    }

    public List<Mpa> getMPA() {
        log.info("Получен запрос на получение списка MPA");
        return mpaStorage.getMPA();
    }
}