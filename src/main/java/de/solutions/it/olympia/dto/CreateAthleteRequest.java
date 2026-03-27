package de.solutions.it.olympia.dto;

import lombok.Data;

@Data
public class CreateAthleteRequest {
    private String name;
    private Long sportId;
    private Long countryId;
}