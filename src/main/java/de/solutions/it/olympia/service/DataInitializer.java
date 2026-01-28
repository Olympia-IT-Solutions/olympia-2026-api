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

        if (sportRepository.count() == 0) {
            seedSports();
        }

        if (userRepository.count() == 0) {
            seedUsers();
        }

        if (athleteRepository.count() == 0) {
            seedAthletes();
        }

        if (resultRepository.count() == 0) {
            seedResultsAndMedals();
        }
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

    private void seedResultsAndMedals() {
        Sport biathlon = sportRepository.findByName("Biathlon").orElseThrow();

        Athlete a1 = athleteRepository.findByName("Max Mustermann").orElseThrow();
        Athlete a2 = athleteRepository.findByName("John Doe").orElseThrow();

        User admin = userRepository.findByUsername("admin").orElseThrow();

        // Result 1 (APPROVED)
        Result r1 = new Result();
        r1.setAthlete(a1);
        r1.setSport(biathlon);
        r1.setCreatedBy(admin);
        r1.setApprovedBy(admin);
        r1.setValue("00:25:34.5");
        r1.setStatus(ResultStatus.APPROVED);
        r1.setActive(true);
        r1 = resultRepository.save(r1);

        // Result 2 (APPROVED)
        Result r2 = new Result();
        r2.setAthlete(a2);
        r2.setSport(biathlon);
        r2.setCreatedBy(admin);
        r2.setApprovedBy(admin);
        r2.setValue("00:26:10.1");
        r2.setStatus(ResultStatus.APPROVED);
        r2.setActive(true);
        r2 = resultRepository.save(r2);

        // Medaillen referenzieren Results (Option B)
        createMedal(r1, MedalType.GOLD);
        createMedal(r2, MedalType.SILVER);
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

    private void createMedal(Result result, MedalType type) {
        Medal m = new Medal();
        m.setResult(result);
        m.setMedalType(type);
        m.setDate(LocalDate.of(2026, 2, 10));
        m.setActive(true);
        medalRepository.save(m);
    }
}
