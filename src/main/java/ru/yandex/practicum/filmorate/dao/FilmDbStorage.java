package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    Long id;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaStorage mpaStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        id = 0L;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(mpaStorage.getMpaByID(resultSet.getInt("rating_id")))
                .genres(genreStorage.getFilmGenresToSet(resultSet.getLong("id")))
                .build();
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film create(Film film) {
        log.info("Получен запрос на создание фильма");
        if (isValid(film)) {
            String sqlQuery = "insert into films (id, name, description, release_date, duration, rating_id) " +
                    "values (?, ?, ?, ?, ?, ?)";
            film.setId(++id);
            if (film.getGenres() != null) {
                genreStorage.setFilmGenres(film);
            }
            film.setMpa(mpaStorage.getMpaByID(film.getMpa().getId()));
            int rowsAffected = jdbcTemplate.update(sqlQuery,
                    film.getId(),
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId()
            );
            if (rowsAffected == 0) {
                throw new FilmNotFoundException("Фильм не обновлён");
            }
        }
        genreStorage.addGenreToFilm(film);
        return film;
    }

    public Film deleteFilm(Long id) {
        log.info("Получен запрос на удаление фильма({})", id);
        Film film = getFilmById(id);
        if (film != null) {
            SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select " +
                    "id, " +
                    "name, " +
                    "description, " +
                    "release_date, " +
                    "duration, " +
                    "rating_id " +
                    "from films where id = ?", id);
            String sqlQuery = "delete from films where id = ?";
            if (filmRows.next()) {
                try {
                    return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
                } catch (EmptyResultDataAccessException e) {
                    throw new FilmNotFoundException("Фильм не удалён");
                }
            } else {
                throw new FilmNotFoundException("Фильм не найден");
            }
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Получен запрос на обновление фильма");
        if (getFilmById(film.getId()) != null || isValid(film)) {
            String sqlQuery =
                    "update films set " +
                            "id = ?, name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?" +
                            "where id = ?";
            if (film.getGenres() != null) {
                genreStorage.setFilmGenres(film);
                genreStorage.addGenreToFilm(film);
            }
            film.setMpa(mpaStorage.getMpaByID(film.getMpa().getId()));
            int rowsAffected = jdbcTemplate.update(sqlQuery,
                    film.getId(),
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId()
            );
            if (rowsAffected == 0) {
                throw new FilmNotFoundException("Фильм не обновлён");
            }
            return film;
        } else {
            throw new FilmNotFoundException("Обновляемый фильм не найден");
        }
    }

    @Override
    public Film getFilmById(Long filmId) {
        String sqlQuery = "select id, name, description, release_date, duration, rating_id from films where id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, filmId);
        if (filmRows.next()) {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
        } else {
            log.info("Фильм с идентификатором {} не найден.", filmId);
            throw new FilmNotFoundException("Фильм не найден");
        }
    }

    public boolean isValid(Film film) {
        if (film.getName() == null || film.getName().isBlank() || film.getName().isEmpty()) {
            throw new ValidationException("Название фильма не должно быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма должно быть пустым или не больше 200 символов: " +
                    film.getDescription().length());
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