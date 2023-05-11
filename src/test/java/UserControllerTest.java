//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.controller.UserController;
//import ru.yandex.practicum.filmorate.exception.ValidationException;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.service.UserService;
//import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
//import ru.yandex.practicum.filmorate.storage.UserStorage;
//
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//public class UserControllerTest {
//    protected User user;
//    private UserController userController;
//
//    private UserStorage userStorage;
//    private UserService userService;
//
//    @BeforeEach
//    public void beforeEach() {
//        userStorage = new InMemoryUserStorage();
//        userController = new UserController(userStorage, new UserService(userStorage));
//        user = User.builder()
//                .name("Bogdan")
//                .login("casumazu")
//                .email("sergeev.bog@yandex.ru")
//                .birthday(LocalDate.of(2002, 3, 12))
//                .build();
//    }
//
//    @Test
//    @DisplayName("Добавление пользователя и сравнение с переданным и полученным значением")
//    public void addUser() {
//        User user1 = userController.create(user);
//        assertEquals(user, user1, "Пользователи должны совпадать");
//        assertEquals(1, userController.getUsers().size(), "Размер списка должен быть равен 1");
//    }
//
//    @Test
//    @DisplayName("Добавление пользователя без логина")
//    public void addUserWhenLoginIsNull() {
//        user.setLogin(null);
//        assertThrows(ValidationException.class, () -> userController.create(user));
//        assertEquals(0, userController.getUsers().size(), "Список должен быть пустым");
//    }
//
//    @Test
//    @DisplayName("Добавление email пользователя без @ ")
//    public void addEmailNotDOG() {
//        user.setEmail("sergeev.bog.yandex.ru");
//        assertThrows(ValidationException.class, () -> userController.create(user));
//        assertEquals(0, userController.getUsers().size(), "Список должен быть пустым");
//    }
//
//    @Test
//    @DisplayName("Добавление пользователя с датой рождения из будущего")
//    public void addUserFromTheFuture() {
//        user.setBirthday(LocalDate.now().plusYears(1));
//        assertThrows(ValidationException.class, () -> userController.create(user));
//        assertEquals(0, userController.getUsers().size(), "Список должен быть пустым");
//    }
//
//    @Test
//    @DisplayName("Добавление пользователя с пробелом в логине")
//    public void addUserWithLoginSpace() {
//        user.setLogin("casu mazu");
//        assertThrows(ValidationException.class, () -> userController.create(user));
//        assertEquals(0, userController.getUsers().size(), "Список должен быть пустым");
//    }
//
//    @Test
//    @DisplayName("Добавление пользователя без имени")
//    public void addUserWithNullName() {
//        user.setName(null);
//        User user1 = userController.create(user);
//        assertEquals(user1.getName(), user.getLogin(), "Логин и имя должны быть одинаковыми");
//    }
//}
