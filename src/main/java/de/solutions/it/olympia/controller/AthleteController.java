package de.solutions.it.olympia.controller;

import de.solutions.it.olympia.model.Athlete;
import de.solutions.it.olympia.repository.AthleteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/athletes")
@RequiredArgsConstructor
public final class AthleteController {

    private final AthleteRepository athleteRepository;

    @GetMapping("/by-sport/{sportId}")
    public List<Athlete> getAthletesBySport(@PathVariable Long sportId) {
        return athleteRepository.findBySportIdAndActiveTrue(sportId);
    }
}
