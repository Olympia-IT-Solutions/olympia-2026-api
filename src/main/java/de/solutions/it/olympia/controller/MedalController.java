package de.solutions.it.olympia.controller;

import de.solutions.it.olympia.dto.AthleteMedalView;
import de.solutions.it.olympia.dto.CountryMedalSummary;
import de.solutions.it.olympia.service.MedalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medals")
@RequiredArgsConstructor
public final class MedalController {

    private final MedalService medalService;

    // Medaillenspiegel aller Länder
    @GetMapping("/table")
    public List<CountryMedalSummary> getMedalTable() {
        return medalService.getCountryMedalTable();
    }

    // Medaillenspiegel für ein bestimmtes Land (z.B. GER)
    @GetMapping("/by-country/{country}")
    public List<AthleteMedalView> getMedalsByCountry(@PathVariable String country) {
        return medalService.getMedalsByCountry(country);
    }
}
