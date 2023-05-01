import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {

    private Film film;
    private FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
        film = Film.builder()
                .name("Lorem ipsum")
                .description("Lorem ipsum dolor sit amet")
                .releaseDate(LocalDate.of(1997, 6, 26))
                .duration(114)
                .build();
    }

    @Test
    @DisplayName("Добавление фильм и сравнение с переданным и полученным значением")
    public void addFilm() {
        Film film1 = filmController.create(film);
        assertEquals(film, film1, "Фильмы должны совпадать");
        assertEquals(1, filmController.getFilms().size(), "В списке должен быть один фильм");
    }

    @Test
    @DisplayName("Установка длительности фильма = 0")
    public void setDurationFilmIs0() {
        film.setDuration(0);
        assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals(0, filmController.getFilms().size(), "Список должен быть пустым");
    }

    @Test
    @DisplayName("Установка отрицательной длительности фильм")
    public void addFilmWhenDurationNegative() {
        film.setDuration(-1);
        assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals(0, filmController.getFilms().size(), "Список должен быть пустым");
    }

    @Test
    @DisplayName("Изменение описания фильма на +200 символов")
    public void addFilmWhenFilmDescriptionMore200() {
        film.setDescription(new String(new char[203]));
        assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals(0, filmController.getFilms().size(), "Список должен быть пустым");
    }

    @Test
    @DisplayName("Изменение даты фильма меньше валидной")
    public void dateFilmMIN() {
        film.setReleaseDate(LocalDate.of(1796, 6, 15));
        assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals(0, filmController.getFilms().size(), "Список должен быть пустым");
    }

    @Test
    @DisplayName("Установка null в название фильма")
    public void setNameFilmIsNull() {
        film.setName(null);
        assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals(0, filmController.getFilms().size(), "Список должен быть пустым");
    }

}
