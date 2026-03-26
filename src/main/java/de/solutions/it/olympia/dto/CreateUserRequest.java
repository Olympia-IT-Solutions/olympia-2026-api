package de.solutions.it.olympia.dto;

import de.solutions.it.olympia.model.UserRole;
import lombok.Data;

@Data
public class CreateUserRequest {
    private String name;
    private String username;
    private String password;
    private UserRole role;
}