package de.solutions.it.olympia.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AthleteListItemDto {
    Long id;
    String name;

    Long countryId;
    String countryCode;
    String countryName;

    Long sportId;
    String sportName;

    boolean active;
}