package de.solutions.it.olympia.controller;

import de.solutions.it.olympia.dto.CreateResultRequest;
import de.solutions.it.olympia.model.*;
import de.solutions.it.olympia.repository.AthleteRepository;
import de.solutions.it.olympia.repository.ResultRepository;
import de.solutions.it.olympia.repository.SportRepository;
import de.solutions.it.olympia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public final class ResultController {

    private final ResultRepository resultRepository;
    private final AthleteRepository athleteRepository;
    private final SportRepository sportRepository;
    private final UserRepository userRepository;

    @GetMapping("/by-sport/{sportId}")
    public List<Result> getResultsBySport(@PathVariable Long sportId) {
        return resultRepository.findBySportIdAndStatusAndActiveTrue(
                sportId,
                ResultStatus.APPROVED
        );
    }

    @PostMapping
    public ResponseEntity<Result> createResult(
            @RequestBody CreateResultRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !user.isActive()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Optional<Sport> sportOpt = sportRepository.findById(request.getSportId());
        if (sportOpt.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        Sport sport = sportOpt.get();

        Optional<Athlete> athleteOpt = athleteRepository.findById(request.getAthleteId());
        if (athleteOpt.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        Athlete athlete = athleteOpt.get();

        // Athlet muss zur Sportart passen
        if (!athlete.getSport().getId().equals(sport.getId())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (request.getValue() == null || request.getValue().isBlank()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Result result = new Result();
        result.setAthlete(athlete);
        result.setSport(sport);
        result.setCreatedBy(user);
        result.setApprovedBy(null);
        result.setValue(request.getValue());
        result.setStatus(ResultStatus.PENDING);
        result.setActive(true);

        Result saved = resultRepository.save(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Result> approveResult(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Optional<Result> resultOpt = resultRepository.findById(id);
        if (resultOpt.isEmpty()) return ResponseEntity.notFound().build();
        Result result = resultOpt.get();

        if (result.getStatus() != ResultStatus.PENDING) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        String username = authentication.getName();
        User approver = userRepository.findByUsername(username).orElse(null);
        if (approver == null || !approver.isActive()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if (result.getCreatedBy().getId().equals(approver.getId())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        result.setApprovedBy(approver);
        result.setStatus(ResultStatus.APPROVED);

        Result saved = resultRepository.save(result);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{id}/invalidate")
    public ResponseEntity<Result> invalidateResult(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String username = authentication.getName();
        User admin = userRepository.findByUsername(username).orElse(null);

        if (admin == null || !admin.isActive() || admin.getRole() != UserRole.ADMIN) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Optional<Result> resultOpt = resultRepository.findById(id);
        if (resultOpt.isEmpty()) return ResponseEntity.notFound().build();
        Result result = resultOpt.get();

        result.setActive(false);
        result.setStatus(ResultStatus.REJECTED);

        Result saved = resultRepository.save(result);
        return ResponseEntity.ok(saved);
    }
}
