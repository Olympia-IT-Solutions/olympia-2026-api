package de.solutions.it.olympia.controller;

import de.solutions.it.olympia.model.Result;
import de.solutions.it.olympia.model.ResultStatus;
import de.solutions.it.olympia.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultRepository resultRepository;

    @GetMapping("/by-sport/{sportId}")
    public List<Result> getResultsBySport(@PathVariable Long sportId) {
        return resultRepository.findBySportIdAndStatusAndActiveTrue(
                sportId,
                ResultStatus.APPROVED
        );
    }
}
