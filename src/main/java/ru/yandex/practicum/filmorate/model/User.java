package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class User {
    private Integer id;
    @NotNull
    private String name;
    @Email(message = "Email is not valid")
    private String email;
    @NotBlank
    private String login;
    @Past
    private LocalDate birthday;

}
