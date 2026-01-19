package com.helpdesk.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();

        // Обработка ошибок "не найден/не найдена" как 404
        if (message != null && (message.contains("не найден") || message.contains("не найдена"))) {
            Map<String, String> error = new HashMap<>();
            error.put("error", message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        // Обработка ошибок "уже занят" как 409 Conflict
        if (message != null && message.contains("уже занят")) {
            Map<String, String> error = new HashMap<>();
            error.put("error", message);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        // Все остальные RuntimeException возвращаем как 500
        Map<String, String> error = new HashMap<>();
        error.put("error", message != null ? message : "Внутренняя ошибка сервера");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
