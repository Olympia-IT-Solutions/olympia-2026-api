package de.solutions.it.olympia.dto;

import de.solutions.it.olympia.model.UserRole;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserListItemDto {
    Long id;
    String name;
    String username;
    UserRole role;
    boolean active;
}