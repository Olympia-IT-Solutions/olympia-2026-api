package de.solutions.it.olympia.controller;

import de.solutions.it.olympia.dto.HealthResponse;
import de.solutions.it.olympia.repository.AthleteRepository;
import de.solutions.it.olympia.repository.ResultRepository;
import de.solutions.it.olympia.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final SportRepository sportRepository;
    private final AthleteRepository athleteRepository;
    private final ResultRepository resultRepository;

    @Value("${spring.application.name:olympia}")
    private String appName;

    @GetMapping
    public ResponseEntity<HealthResponse> health() {

        boolean dbUp;
        String dbMessage;
        long sportsCount = 0;
        long athletesCount = 0;
        long resultsCount = 0;

        try {
            sportsCount = sportRepository.count();
            athletesCount = athleteRepository.count();
            resultsCount = resultRepository.count();

            dbUp = true;
            dbMessage = "OK";
        } catch (Exception ex) {
            dbUp = false;
            dbMessage = ex.getClass().getSimpleName() + ": " + ex.getMessage();
        }

        HealthResponse response = HealthResponse.builder()
                .appName(appName)
                .status(dbUp ? "UP" : "DOWN")
                .databaseUp(dbUp)
                .databaseMessage(dbMessage)
                .sportsCount(sportsCount)
                .athletesCount(athletesCount)
                .resultsCount(resultsCount)
                .timestamp(Instant.now())
                .build();

        HttpStatus status = dbUp ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return new ResponseEntity<>(response, status);
    }
}
