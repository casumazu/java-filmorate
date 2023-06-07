package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre getGenreByID(Integer id) {
        if (id <= 0) {
            throw new ValidationException("ID жанра меньше или ровно 0");
        }
        String str = "select * from genres where id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(str, id);
        if (userRows.first()) {
            return jdbcTemplate.query(str, this::mapRowToGenre, id).stream().findFirst()
                    .orElseThrow(() -> new GenreNotFoundException("Жанр не найден"));
        } else {
            log.info("Жанр с идентификатором {} не найден.", id);
            throw new GenreNotFoundException("Жанр не найден");
        }
    }

    public List<Genre> getFilmGenres(Long filmId) {
        String sql = "select genre_id, name FROM film_genres" +
                " inner join genres ON genre_id = id where film_id = ?";
        return jdbcTemplate.query(sql, new Object[]{filmId}, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"), rs.getString("name"))
        );
    }

    public List<Genre> getGenre() {
        String sql = "select * from genres";
        return jdbcTemplate.query(sql, (rs, RowNum) -> new Genre(
                rs.getInt("id"),
                rs.getString("name"))

        );
    }

    public void addGenreToFilm(Film film) {
        if (film.getGenres() != null) {
            String sqlDelete = "delete from film_genres where film_id = ?";

            jdbcTemplate.update(sqlDelete, film.getId());

            String sql = "insert into film_genres (film_id, genre_id) values (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sql, film.getId(), genre.getId());
            }
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }

    public Set<Genre> getFilmGenresToSet(Long filmId) {
        return new HashSet<>(getFilmGenres(filmId));
    }

    public void setFilmGenres(Film film) {
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                genre.setName(getGenreByID(genre.getId()).getName());
            }
            Collection<Genre> sortGenres = film.getGenres().stream()
                    .sorted(Comparator.comparing(Genre::getId))
                    .collect(Collectors.toList());
            film.setGenres(new LinkedHashSet<>(sortGenres));
        }
    }
}