package de.solutions.it.olympia.dto;

import de.solutions.it.olympia.model.UserRole;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String username;
    private UserRole role;
}