package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
public class Likes {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public Likes(JdbcTemplate jdbcTemplate, MpaStorage mpaStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public void addLike(Long filmId, Long userId) {
        String sql = "insert into film_likes (film_id, user_id) values (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        String sql = "delete from film_likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public List<Film> getPopular(Integer count) {
        String sqlQuery =
                "select " +
                        "id, " +
                        "name, " +
                        "description, " +
                        "release_date, " +
                        "duration, " +
                        "rating_id " +
                        "from films " +
                        "left join film_likes on films.id = film_likes.film_id " +
                        "group by films.id order by count(film_likes.user_id) desc limit ?";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new Film(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("release_Date").toLocalDate(),
                        rs.getInt("duration"),
                        mpaStorage.getMpaByID(rs.getInt("rating_id")),
                        genreStorage.getFilmGenresToSet(rs.getLong("id"))),
                count);
    }

    public List<Long> getLikes(Long filmId) {
        String sqlQuery = "select user_id from film_likes where film_id = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }
}
