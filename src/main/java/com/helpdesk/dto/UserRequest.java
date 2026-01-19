package com.helpdesk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank(message = "Логин не может быть пустым")
    private String login;

    private String password;

    @NotBlank(message = "ФИО не может быть пустым")
    private String fullName;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный email")
    private String email;

    @NotNull(message = "Роль обязательна")
    private Long roleId;
}
