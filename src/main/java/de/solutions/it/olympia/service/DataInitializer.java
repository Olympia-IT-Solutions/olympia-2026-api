package de.solutions.it.olympia.service;

import de.solutions.it.olympia.model.*;
import de.solutions.it.olympia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SportRepository sportRepository;
    private final AthleteRepository athleteRepository;
    private final UserRepository userRepository;
    private final ResultRepository resultRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (sportRepository.count() > 0) {
            return;
        }

        // --- Sportarten ---
        Sport biathlon     = createSport("Biathlon");
        Sport bobsport     = createSport("Bobsport");
        Sport curling      = createSport("Curling");
        Sport eishockey    = createSport("Eishockey");
        Sport eiskunstlauf = createSport("Eiskunstlauf");
        Sport skilanglauf  = createSport("Skilanglauf");
        Sport skispringen  = createSport("Skispringen");

        // --- Benutzer ---
        User admin    = createUser("Admin",       "admin", UserRole.ADMIN);
        User referee1 = createUser("Referee One", "ref1",  UserRole.REFEREE);
        User referee2 = createUser("Referee Two", "ref2",  UserRole.REFEREE);

        // --- Athleten ---
        Athlete a1 = createAthlete("Max Mustermann", "GER", biathlon);
        Athlete a2 = createAthlete("John Doe",       "USA", biathlon);
        Athlete a3 = createAthlete("Anna Svensson",  "SWE", skispringen);

        // Beispiel-Ergebnis
        Result r1 = new Result();
        r1.setAthlete(a1);
        r1.setSport(biathlon);
        r1.setCreatedBy(admin);
        r1.setApprovedBy(admin);
        r1.setValue("00:25:34.5");
        r1.setStatus(ResultStatus.APPROVED);
        r1.setActive(true);
        resultRepository.save(r1);
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

    private User createUser(String name, String username, UserRole role) {
        User u = new User();
        u.setName(name);
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode("test123")); // Login-Passwort
        u.setRole(role);
        u.setActive(true);
        return userRepository.save(u);
    }
}
