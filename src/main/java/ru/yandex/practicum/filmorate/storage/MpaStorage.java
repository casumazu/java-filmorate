package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import javax.validation.ValidationException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa getMpaByID(Integer id) {
        if (id <= 0) {
            throw new ValidationException("ID MPA меньше или ровно 0");
        }
        String sql = "select * from ratings_MPA where id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        if (userRows.next()) {
            return jdbcTemplate.query(sql, this::mapRowToRatingMPA, id).stream().findFirst()
                    .orElseThrow(() -> new MpaNotFoundException("MPA не найден"));
        } else {
            log.info("MPA с идентификатором {} не найден.", id);
            throw new MpaNotFoundException("MPA не найден");
        }
    }

    public List<Mpa> getMPA() {
        String sql = "select * from ratings_MPA";
        return jdbcTemplate.query(sql, (rs, RowNum) -> new Mpa(
                rs.getInt("id"),
                rs.getString("name"))
        );
    }

    private Mpa mapRowToRatingMPA(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}