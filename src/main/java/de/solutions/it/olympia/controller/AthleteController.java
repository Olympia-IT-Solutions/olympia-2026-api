package de.solutions.it.olympia.controller;

import de.solutions.it.olympia.dto.AthleteListItemDto;
import de.solutions.it.olympia.dto.CreateAthleteRequest;
import de.solutions.it.olympia.dto.UpdateAthleteRequest;
import de.solutions.it.olympia.model.Athlete;
import de.solutions.it.olympia.model.Country;
import de.solutions.it.olympia.model.Sport;
import de.solutions.it.olympia.repository.AthleteRepository;
import de.solutions.it.olympia.repository.CountryRepository;
import de.solutions.it.olympia.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/athletes")
@RequiredArgsConstructor
public class AthleteController {

    private final AthleteRepository athleteRepository;
    private final SportRepository sportRepository;
    private final CountryRepository countryRepository;

    @GetMapping
    public List<AthleteListItemDto> getAllAthletes() {
        return athleteRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @PostMapping
    public ResponseEntity<AthleteListItemDto> createAthlete(@RequestBody CreateAthleteRequest req) {
        Sport sport = sportRepository.findById(req.getSportId()).orElse(null);
        if (sport == null) {
            return ResponseEntity.badRequest().build();
        }

        Country country = countryRepository.findById(req.getCountryId()).orElse(null);
        if (country == null) {
            return ResponseEntity.badRequest().build();
        }

        Athlete athlete = new Athlete();
        athlete.setName(req.getName());
        athlete.setSport(sport);
        athlete.setCountry(country);
        athlete.setActive(true);

        Athlete saved = athleteRepository.save(athlete);
        return ResponseEntity.status(201).body(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AthleteListItemDto> updateAthlete(
            @PathVariable Long id,
            @RequestBody UpdateAthleteRequest req
    ) {
        if (req.getId() == null || !req.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        Athlete athlete = athleteRepository.findById(id).orElse(null);
        if (athlete == null) {
            return ResponseEntity.notFound().build();
        }

        if (req.getName() != null && !req.getName().isBlank()) {
            athlete.setName(req.getName());
        }

        if (req.getSportId() != null) {
            Sport sport = sportRepository.findById(req.getSportId()).orElse(null);
            if (sport == null) {
                return ResponseEntity.badRequest().build();
            }
            athlete.setSport(sport);
        }

        if (req.getCountryId() != null) {
            Country country = countryRepository.findById(req.getCountryId()).orElse(null);
            if (country == null) {
                return ResponseEntity.badRequest().build();
            }
            athlete.setCountry(country);
        }

        Athlete saved = athleteRepository.save(athlete);
        return ResponseEntity.ok(toDto(saved));
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateAthlete(@PathVariable Long id) {
        Athlete athlete = athleteRepository.findById(id).orElse(null);
        if (athlete == null) {
            return ResponseEntity.notFound().build();
        }

        athlete.setActive(false);
        athleteRepository.save(athlete);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activateAthlete(@PathVariable Long id) {
        Athlete athlete = athleteRepository.findById(id).orElse(null);
        if (athlete == null) {
            return ResponseEntity.notFound().build();
        }

        athlete.setActive(true);
        athleteRepository.save(athlete);

        return ResponseEntity.ok().build();
    }

    private AthleteListItemDto toDto(Athlete athlete) {
        return AthleteListItemDto.builder()
                .id(athlete.getId())
                .name(athlete.getName())
                .countryId(athlete.getCountry() != null ? athlete.getCountry().getId() : null)
                .countryCode(athlete.getCountry() != null ? athlete.getCountry().getCode() : null)
                .countryName(athlete.getCountry() != null ? athlete.getCountry().getName() : null)
                .sportId(athlete.getSport() != null ? athlete.getSport().getId() : null)
                .sportName(athlete.getSport() != null ? athlete.getSport().getName() : null)
                .active(athlete.isActive())
                .build();
    }
}