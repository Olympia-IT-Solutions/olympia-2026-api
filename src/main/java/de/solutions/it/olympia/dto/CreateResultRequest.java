package de.solutions.it.olympia.dto;

import lombok.Data;

@Data
public class CreateResultRequest {

    private Long athleteId;
    private Long sportId;
    private String value;
}
