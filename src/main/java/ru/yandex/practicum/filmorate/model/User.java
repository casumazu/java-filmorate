package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;
    @NotEmpty
    private String name;
    @Email(message = "Email введён не верно")
    private String email;
    @NotBlank
    private String login;
    @Past
    private LocalDate birthday;

    Set<Long> friends = new HashSet<>();

}
