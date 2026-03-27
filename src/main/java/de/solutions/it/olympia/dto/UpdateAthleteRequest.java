package de.solutions.it.olympia.dto;

import lombok.Data;

@Data
public class UpdateAthleteRequest {
    private Long id;
    private String name;
    private Long sportId;
    private Long countryId;
}