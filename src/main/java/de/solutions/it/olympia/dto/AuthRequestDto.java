package de.solutions.it.olympia.dto;

import lombok.Data;

@Data
public class AuthRequestDto {
    private String username;
    private String password;
}