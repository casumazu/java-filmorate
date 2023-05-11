package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import javax.validation.constraints.*;

@Data
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @PastOrPresent
    private LocalDate releaseDate;
    @PositiveOrZero
    private Integer duration;
    Set<Long> likes = new HashSet<>();
}
