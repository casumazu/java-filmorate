package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    Long id;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        id = 0L;
    }

    @Override
    public User getUser(Long id) {
        if (id <= 0) {
            throw new UserNotFoundException("ID пользователя меньше или ровно 0");
        }
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select id, email, name, login, " +
                "birthday FROM users WHERE id = ?", id);
        String sqlQuery = "select id, name, email, login, birthday FROM users WHERE id = ?";
        if (userRows.next()) {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public List<User> getUsers() {
        String sql = "select * from users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User create(User user) {
        if (getUsers().contains((user))) {
            log.trace("Пользователь уже существует:{}.", user);
            throw new ValidationException("Данный пользователь уже существует");
        }
        if (isValid(user)) {
            user.setId(++id);
            String sqlQuery = "insert into users(id, email, login, name, birthday) " +
                    "values (?, ?, ?, ?, ?)";

            Object[] params = new Object[]{
                    user.getId(),
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday()
            };
            jdbcTemplate.update(sqlQuery, params);
        }
        return user;
    }

    public User deleteUser(Long id) {
        User user = getUser(id);
        if (getUser(id) != null) {
            SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where id = ?", id);
            String sqlQuery = "delete from users where id = ?";
            if (userRows.next()) {
                jdbcTemplate.update(sqlQuery,
                        user.getId());
                return user;

            }
        }
        return user;
    }

    @Override
    public User update(User user) {
        if (getUser(user.getId()) != null || isValid(user)) {
            String sqlQuery = "update users set " +
                    "id = ?, email = ?, login = ?, name = ?, birthday = ? " +
                    "where id = ?";
            jdbcTemplate.update(sqlQuery,
                    user.getId(),
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId()
            );
            return user;
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public void addFriend(Long userId, Long friendId) {
        if (getFriends(userId).contains(getUser(friendId))) {
            throw new ValidationException("Дружба пользователей уже существует");
        }
        if (getUser(userId) != null || getUser(friendId) != null) {
            String sql = "insert into friends (user_id, friend_id, status) values (?, ?, ?)";
            Object[] params = new Object[]{userId, friendId, true};
            jdbcTemplate.update(sql, params);
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        if (user.getId() != null || friend.getId() != null) {
            String sql = "delete from friends" +
                    " where user_id = ? and friend_id = ?";
            jdbcTemplate.update(sql, userId, friendId);
        }
    }

    public List<User> getFriends(Long userId) {
        String sql = "select id, email, login, name, birthday from friends " +
                "inner join users on users.id = friends.friend_id " +
                "where user_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, userId);
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        User user1 = getUser(userId);
        User user2 = getUser(friendId);
        Set<User> intersection = null;
        if ((user1 != null) && (user2 != null)) {
            intersection = new HashSet<>(getFriends(userId));
            intersection.retainAll(getFriends(friendId));
        }
        assert intersection != null;
        return new ArrayList<>(intersection);
    }

    private boolean isValid(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный e-mail: " + user.getEmail());
        }
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Некорректный логин: " + user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Некорректная дата рождения: " + user.getBirthday());
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return true;
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()
        );
    }
}
