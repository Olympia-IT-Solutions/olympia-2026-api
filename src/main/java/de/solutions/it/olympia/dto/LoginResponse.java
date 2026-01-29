package de.solutions.it.olympia.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginResponse {
    String token;
    String tokenType; // "Bearer"
    long expiresInSeconds;
}
