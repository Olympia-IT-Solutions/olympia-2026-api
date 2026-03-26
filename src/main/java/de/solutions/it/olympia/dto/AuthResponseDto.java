package de.solutions.it.olympia.dto;

import de.solutions.it.olympia.model.UserRole;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthResponseDto {
    String token;
    String username;
    UserRole role;
}