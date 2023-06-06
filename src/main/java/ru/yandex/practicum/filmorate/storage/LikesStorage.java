package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
@Slf4j
public class LikesStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public LikesStorage(JdbcTemplate jdbcTemplate, MpaStorage mpaStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Запрос за добавление лайка фильму - ID{} от пользователя - ID{} ", filmId, userId);
        String sql = "insert into film_likes (film_id, user_id) values (?, ?)";
        int rowsAffected = jdbcTemplate.update(sql, filmId, userId);
        if (rowsAffected == 0) {
            throw new UserNotFoundException("Лайк не поставлен");
        }
    }

    public void deleteLike(Long filmId, Long userId) {
        log.info("Получен запрос на удаление лайка фильма({}) от пользователя({})", filmId, userId);
        String sql = "delete from film_likes where film_id = ? and user_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, filmId, userId);
        if (rowsAffected == 0) {
            throw new UserNotFoundException("Лайк не удалён");
        }
    }

    public List<Film> getPopular(Integer count) {
        log.info("Получен запрос на получение популярных фильмов (count = {})", count);
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

        return jdbcTemplate.query(sqlQuery, new Object[]{count}, (rs, rowNum) -> new Film(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_Date").toLocalDate(),
                rs.getInt("duration"),
                mpaStorage.getMpaByID(rs.getInt("rating_id")),
                genreStorage.getFilmGenresToSet(rs.getLong("id"))));
    }

    public List<Long> getLikes(Long filmId) {
        log.info("Получен запрос на получение лайков фильма({})", filmId);
        String sqlQuery = "select user_id from film_likes where film_id = ?";
        return jdbcTemplate.query(sqlQuery, new Object[]{filmId}, (rs, rowNum) ->
                rs.getLong("user_id"));
    }
}