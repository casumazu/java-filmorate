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
    Set<Long> likes;

    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration, Set<Long> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = likes;
        if (likes == null) {
            this.likes = new HashSet<>();
        }
    }
}
