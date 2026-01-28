package de.solutions.it.olympia.service;

import de.solutions.it.olympia.model.*;
import de.solutions.it.olympia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SportRepository sportRepository;
    private final AthleteRepository athleteRepository;
    private final UserRepository userRepository;
    private final ResultRepository resultRepository;
    private final MedalRepository medalRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // 1) Sportarten einmalig anlegen
        if (sportRepository.count() == 0) {
            seedSports();
        }

        // 2) Users einmalig anlegen
        if (userRepository.count() == 0) {
            seedUsers();
        }

        // 3) Athleten einmalig anlegen
        if (athleteRepository.count() == 0) {
            seedAthletes();
        }

        // 4) Results einmalig anlegen (optional)
        if (resultRepository.count() == 0) {
            seedResults();
        }

        // 5) Medals einmalig anlegen (optional)
        if (medalRepository.count() == 0) {
            seedMedals();
        }
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
        u.setPassword(passwordEncoder.encode("test123"));
        u.setRole(role);
        u.setActive(true);
        return userRepository.save(u);
    }

    private void createMedal(Athlete athlete, MedalType type) {
        Medal m = new Medal();
        m.setAthlete(athlete);
        m.setMedalType(type);
        m.setDate(LocalDate.of(2026, 2, 10));
        m.setActive(true);
        medalRepository.save(m);
    }

    private void seedSports() {
        createSport("Biathlon");
        createSport("Bobsport");
        createSport("Curling");
        createSport("Eishockey");
        createSport("Eiskunstlauf");
        createSport("Skilanglauf");
        createSport("Skispringen");
    }

    private void seedUsers() {
        createUser("Admin", "admin", UserRole.ADMIN);
        createUser("Referee One", "ref1", UserRole.REFEREE);
        createUser("Referee Two", "ref2", UserRole.REFEREE);
    }

    private void seedAthletes() {
        Sport biathlon = sportRepository.findByName("Biathlon").orElseThrow();
        Sport skispringen = sportRepository.findByName("Skispringen").orElseThrow();

        createAthlete("Max Mustermann", "GER", biathlon);
        createAthlete("John Doe", "USA", biathlon);
        createAthlete("Anna Svensson", "SWE", skispringen);
    }

    private void seedResults() {
        Sport biathlon = sportRepository.findByName("Biathlon").orElseThrow();
        Athlete a1 = athleteRepository.findByName("Max Mustermann").orElseThrow();
        User admin = userRepository.findByUsername("admin").orElseThrow();

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

    private void seedMedals() {
        Athlete a1 = athleteRepository.findByName("Max Mustermann").orElseThrow();
        Athlete a2 = athleteRepository.findByName("John Doe").orElseThrow();
        Athlete a3 = athleteRepository.findByName("Anna Svensson").orElseThrow();

        createMedal(a1, MedalType.GOLD);
        createMedal(a2, MedalType.SILVER);
        createMedal(a3, MedalType.BRONZE);
    }

}
