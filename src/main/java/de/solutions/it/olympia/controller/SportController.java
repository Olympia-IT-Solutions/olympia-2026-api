package de.solutions.it.olympia.controller;

import de.solutions.it.olympia.model.Sport;
import de.solutions.it.olympia.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sports")
@RequiredArgsConstructor
public final class SportController {

    private final SportRepository repository;

    @GetMapping
    public List<Sport> getAllSports(){
        return repository.findAll();
    }

}
