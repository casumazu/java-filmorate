import org.junit.jupiter.api.BeforeEach;
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
                .releaseDate(LocalDate.of(1997,6,26))
                .duration(114)
                .build();
    }
    @Test
    public void addFilm() {
        Film film1 = filmController.add(film);
        assertEquals(film, film1, "Фильмы должны совпадать");
        assertEquals(1, filmController.returnFilms().size(), "В списке должен быть один фильм");
    }

    @Test
    public void setDurationFilmIs0() {
        film.setDuration(0);
        assertThrows(ValidationException.class, () -> filmController.add(film));
        assertEquals(0, filmController.returnFilms().size(), "Список должен быть пустым");
    }
    @Test
    public void addFilmWhenDurationNegative() {
        film.setDuration(-1);
        assertThrows(ValidationException.class, () -> filmController.add(film));
        assertEquals(0, filmController.returnFilms().size(), "Список должен быть пустым");
    }

    @Test
    public void addFilmWhenFilmDescriptionMore200() {
        film.setDescription(new String(new char[203]));
        assertThrows(ValidationException.class, () -> filmController.add(film));
        assertEquals(0, filmController.returnFilms().size(), "Список должен быть пустым");
    }

    @Test
    public void dateFilmMIN() {
        film.setReleaseDate(LocalDate.of(1796,6,15));
        assertThrows(ValidationException.class, () -> filmController.add(film));
        assertEquals(0, filmController.returnFilms().size(), "Список должен быть пустым");
    }

    @Test
    public void setNameFilmIsNull() {
        film.setName("");
        assertThrows(ValidationException.class, () -> filmController.add(film));
        assertEquals(0, filmController.returnFilms().size(), "Список должен быть пустым");
    }

    @Test
    public void setDescriptionFilmIsNull() {
        film.setDescription("");
        assertThrows(ValidationException.class, () -> filmController.add(film));
        assertEquals(0, filmController.returnFilms().size(), "Список должен быть пустым");
    }

}
