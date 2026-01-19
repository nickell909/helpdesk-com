package com.helpdesk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketRequest {
    @NotBlank(message = "Заголовок не может быть пустым")
    private String title;

    private String description;

    @NotNull(message = "Категория обязательна")
    private Long categoryId;

    @NotNull(message = "Приоритет обязателен")
    private Long priorityId;
}
