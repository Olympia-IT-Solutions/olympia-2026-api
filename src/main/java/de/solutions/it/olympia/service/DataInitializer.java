package de.solutions.it.olympia.service;

import de.solutions.it.olympia.model.*;
import de.solutions.it.olympia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SportRepository sportRepository;
    private final AthleteRepository athleteRepository;
    private final UserRepository userRepository;
    private final ResultRepository resultRepository;

    @Override
    public void run(String... args) {
        // Nur einmal initial befüllen
        if (sportRepository.count() > 0) {
            return;
        }

        // --- Sportarten ---
        Sport biathlon = createSport("Biathlon");
        Sport bobsport = createSport("Bobsport");
        Sport curling = createSport("Curling");
        Sport eishockey = createSport("Eishockey");
        Sport eiskunstlauf = createSport("Eiskunstlauf");
        Sport skilanglauf = createSport("Skilanglauf");
        Sport skispringen = createSport("Skispringen");

        // --- User (Admin, später fürs Login interessant) ---
        User admin = new User();
        admin.setName("Admin");
        admin.setUsername("admin");
        admin.setPassword("admin");   // später verschlüsseln, jetzt egal
        admin.setAdmin(true);
        admin.setActive(true);
        admin = userRepository.save(admin);

        // --- Beispiel-Athleten ---
        Athlete athlete1 = createAthlete("Max Mustermann", "GER", biathlon);
        Athlete athlete2 = createAthlete("John Doe", "USA", biathlon);
        Athlete athlete3 = createAthlete("Anna Svensson", "SWE", skispringen);

        // --- Beispiel-Ergebnis (APPROVED) ---
        Result result1 = new Result();
        result1.setAthlete(athlete1);
        result1.setSport(biathlon);
        result1.setCreatedBy(admin);
        result1.setApprovedBy(admin);                 // damit es direkt APPROVED ist
        result1.setValue("00:25:34.5");
        result1.setStatus(ResultStatus.APPROVED);
        result1.setActive(true);
        resultRepository.save(result1);
    }

    private Sport createSport(String name) {
        Sport s = new Sport();
        s.setName(name);
        s.setActive(true);
        return sportRepository.save(s);
    }

    private Athlete createAthlete(String name, String country, Sport sport) {
        Athlete a = new Athlete();
        a.setName(name);
        a.setCountry(country);
        a.setSport(sport);
        a.setActive(true);
        return athleteRepository.save(a);
    }
}
