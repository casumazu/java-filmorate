import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private UserStorage userStorage;

    protected User user = new User(
            1L, "sergeev.bog@yandex.ru", "casumazu", "Bogdan",
            LocalDate.of(2002, 3, 12));

    @BeforeEach
    public void beforeEach() {
        userStorage = new InMemoryUserStorage();
    }

    @Test
    @DisplayName("Добавление пользователя и сравнение с переданным и полученным значением")
    public void addUser() {
        User user1 = userStorage.create(user);
        assertEquals(user, user1, "Пользователи должны совпадать");
        assertEquals(1, userStorage.getUsers().size(), "Размер списка должен быть равен 1");
    }

    @Test
    @DisplayName("Добавление пользователя без логина")
    public void addUserWhenLoginIsNull() {
        user.setLogin(null);
        assertThrows(ValidationException.class, () -> userStorage.create(user));
        assertEquals(0, userStorage.getUsers().size(), "Список должен быть пустым");
    }

    @Test
    @DisplayName("Добавление email пользователя без @ ")
    public void addEmailNotDOG() {
        user.setEmail("sergeev.bog.yandex.ru");
        assertThrows(ValidationException.class, () -> userStorage.create(user));
        assertEquals(0, userStorage.getUsers().size(), "Список должен быть пустым");
    }

    @Test
    @DisplayName("Добавление пользователя с датой рождения из будущего")
    public void addUserFromTheFuture() {
        user.setBirthday(LocalDate.now().plusYears(1));
        assertThrows(ValidationException.class, () -> userStorage.create(user));
        assertEquals(0, userStorage.getUsers().size(), "Список должен быть пустым");
    }

    @Test
    @DisplayName("Добавление пользователя с пробелом в логине")
    public void addUserWithLoginSpace() {
        user.setLogin("casu mazu");
        assertThrows(ValidationException.class, () -> userStorage.create(user));
        assertEquals(0, userStorage.getUsers().size(), "Список должен быть пустым");
    }

    @Test
    @DisplayName("Добавление пользователя без имени")
    public void addUserWithNullName() {
        user.setName(null);
        User user1 = userStorage.create(user);
        assertEquals(user1.getName(), user.getLogin(), "Логин и имя должны быть одинаковыми");
    }
}