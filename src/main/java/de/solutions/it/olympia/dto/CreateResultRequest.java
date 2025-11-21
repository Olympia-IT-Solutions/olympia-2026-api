package de.solutions.it.olympia.dto;

import lombok.Data;

@Data
public class CreateResultRequest {

    private Long athleteId; // Sportler welches das Ergebnis erreicht hat
    private Long sportId;   // Sportart
    private String value;   // Zeit / Punkte / etc.
    private Long userId;    // Wettkampfrichter, der das einträgt
}
