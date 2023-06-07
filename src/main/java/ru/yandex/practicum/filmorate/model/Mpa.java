package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.*;

@Data
@Builder
public class Mpa {
    private Integer id;
    private String name;

    @JsonCreator
    public Mpa(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}