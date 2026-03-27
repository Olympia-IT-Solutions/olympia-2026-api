package de.solutions.it.olympia.dto;

import lombok.Data;

@Data
public class UpdateAthleteRequest {
    private String name;
    private Long sportId;
    private String countryCode;
}