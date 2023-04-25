package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@Builder
public class Film {
    private Integer id;
    @javax.validation.constraints.NotNull(message = "Название не должно быть пустым.")
    private String name;
    @Min(value = 1, message = "Описание меньше 200 символов.")
    @Max(value = 200, message = "Описание больше 3000 символов.")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    private Integer duration;
}
