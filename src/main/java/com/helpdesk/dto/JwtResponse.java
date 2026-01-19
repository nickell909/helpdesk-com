package com.helpdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String login;
    private String fullName;
    private String role;

    public JwtResponse(String token, Long userId, String login, String fullName, String role) {
        this.token = token;
        this.userId = userId;
        this.login = login;
        this.fullName = fullName;
        this.role = role;
    }
}
