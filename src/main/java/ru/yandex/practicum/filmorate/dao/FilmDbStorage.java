package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
                .Mpa(mpaStorage.getMpaByID(resultSet.getInt("rating_id")))
                .genres(genreStorage.getFilmGenresToSet(resultSet.getLong("id")))
                .build();
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_Date").toLocalDate(),
                rs.getInt("duration"),
                mpaStorage.getMpaByID(rs.getInt("rating_id")),
                genreStorage.getFilmGenresToSet(rs.getLong("id"))
        ));
    }

    @Override
    public Film create(Film film) {
        if (isValid(film)) {
            String sqlQuery = "insert into films (id, name, description, release_date, duration, rating_id) " +
                    "values (?, ?, ?, ?, ?, ?)";
            film.setId(++id);

            if (film.getGenres() != null) {
                for (Genre genre : film.getGenres()) {
                    genre.setName(genreStorage.getGenreByID(genre.getId()).getName());
                }
                Collection<Genre> sortGenres = film.getGenres().stream()
                        .sorted(Comparator.comparing(Genre::getId))
                        .collect(Collectors.toList());
                film.setGenres(new LinkedHashSet<>(sortGenres));
            }

            film.setMpa(mpaStorage.getMpaByID(film.getMpa().getId()));
            jdbcTemplate.update(sqlQuery,
                    film.getId(),
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId()
            );
        }
        genreStorage.addGenreToFilm(film);
        return film;
    }

    public Film deleteFilm(Long id) {
        Film film = getFilmById(id);
        if (getFilmById(id) != null) {
            SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from films where id = ?", id);
            String sqlQuery = "delete from films where id = ?";
            if (userRows.next()) {
                jdbcTemplate.update(sqlQuery,
                        film.getId());
                return film;

            }
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        if (getFilmById(film.getId()) != null || isValid(film)) {
            String sqlQuery = "update films set " +
                    "id = ?, name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?" +
                    "where id = ?";
            if (film.getGenres() != null) {
                for (Genre genre : film.getGenres()) {
                    genre.setName(genreStorage.getGenreByID(genre.getId()).getName());
                }
                Collection<Genre> sortGenres = film.getGenres().stream()
                        .sorted(Comparator.comparing(Genre::getId))
                        .collect(Collectors.toList());
                film.setGenres(new LinkedHashSet<>(sortGenres));
            }
            genreStorage.addGenreToFilm(film);
            film.setMpa(mpaStorage.getMpaByID(film.getMpa().getId()));
            jdbcTemplate.update(sqlQuery,
                    film.getId(),
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId()
            );
            return film;
        } else {
            throw new FilmNotFoundException("Обновляемый фильм не найден");
        }
    }

    @Override
    public Film getFilmById(Long filmId) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films where id = ?", filmId);
        String sqlQuery = "select * from films where id = ?";
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
            throw new ValidationException("Описание фильма должно быть пустым или не больше 200 символов: " + film.getDescription().length());
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
