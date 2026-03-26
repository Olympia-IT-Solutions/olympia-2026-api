package de.solutions.it.olympia.controller;

import de.solutions.it.olympia.model.Country;
import de.solutions.it.olympia.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryRepository countryRepository;

    @GetMapping
    public List<Country> getAllCountries() {
        return countryRepository.findByActiveTrueOrderByNameAsc();
    }
}