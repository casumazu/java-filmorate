package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@Builder
public class Film {
    private Integer id;
    @NotBlank
    private String name;
    @Min(value = 1, message = "Описание меньше 1 символов.")
    @Max(value = 200, message = "Описание больше 200 символов.")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
}
