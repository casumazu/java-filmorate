package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
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
    @PastOrPresent
    private LocalDate birthday;

}
