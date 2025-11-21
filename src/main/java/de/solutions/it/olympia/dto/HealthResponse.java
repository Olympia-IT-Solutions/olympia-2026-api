package de.solutions.it.olympia.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class HealthResponse {

    String appName;
    String status;          // "UP" oder "DOWN"

    boolean databaseUp;
    String databaseMessage;

    long sportsCount;       // Anzahl Sportarten
    long athletesCount;     // Anzahl Athleten
    long resultsCount;      // Anzahl Ergebnisse

    Instant timestamp;
}
