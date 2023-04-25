import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private User user;
    private UserController userController;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
        user = User.builder()
                .name("Bogdan")
                .login("casumazu")
                .email("sergeev.bog@yandex.ru")
                .birthday(LocalDate.of(2002, 3, 12))
                .build();
    }

    @Test
    public void addUser() {
        User user1 = userController.add(user);
        assertEquals(user,user1,"Пользователи должны совпадать");
        assertEquals(1, userController.returnUsers().size(),"Размер списка должен быть равен 1");
    }

    @Test
    public void addUserWhenLoginIsNull() {
        user.setLogin("");
        assertThrows(ValidationException.class, () -> userController.add(user));
        assertEquals(0, userController.returnUsers().size(), "Список должен быть пустым");
    }

    @Test
    public void addEmailNotDOG() {
        user.setEmail("sergeev.bog.yandex.ru");
        assertThrows(ValidationException.class, () -> userController.add(user));
        assertEquals(0, userController.returnUsers().size(), "Список должен быть пустым");
    }

    @Test
    public void addUserFromTheFuture() {
        user.setBirthday(LocalDate.now().plusYears(1));
        assertThrows(ValidationException.class, () -> userController.add(user));
        assertEquals(0, userController.returnUsers().size(), "Список должен быть пустым");
    }

    @Test
    public void addUserWithLoginSpace() {
        user.setLogin("casu mazu");
        assertThrows(ValidationException.class, () -> userController.add(user));
        assertEquals(0, userController.returnUsers().size(), "Список должен быть пустым");
    }
}
