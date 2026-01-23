package de.solutions.it.olympia.controller;

import de.solutions.it.olympia.dto.CreateResultRequest;
import de.solutions.it.olympia.dto.ResultListItemDto;
import de.solutions.it.olympia.model.*;
import de.solutions.it.olympia.repository.*;
import de.solutions.it.olympia.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultRepository resultRepository;
    private final AthleteRepository athleteRepository;
    private final SportRepository sportRepository;
    private final UserRepository userRepository;
    private final MedalRepository medalRepository;


    /**
     * Ergebnistabelle für einen Sport:
     * - 50 Einträge pro Seite (Default)
     * - optionaler Länderfilter (?country=GER)
     * - liefert pro Zeile auch die Medaille des Athleten in diesem Sport
     */
    @GetMapping("/by-sport/{sportId}")
    public Page<ResultListItemDto> getResultsBySport(
            @PathVariable Long sportId,
            @RequestParam(required = false) String country,
            @PageableDefault(
                    size = 50,
                    sort = "value",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<Result> page;

        if (country != null && !country.isBlank()) {
            page = resultRepository
                    .findBySport_IdAndAthlete_CountryAndActiveTrue(sportId, country, pageable);
        } else {
            page = resultRepository
                    .findBySport_IdAndActiveTrue(sportId, pageable);
        }

        return page.map(result -> {
            Athlete athlete = result.getAthlete();
            Sport sport = result.getSport();

            var medalOpt = medalRepository
                    .findFirstByAthlete_IdAndAthlete_Sport_IdAndActiveTrue(athlete.getId(), sport.getId());

            MedalType medalType = medalOpt
                    .map(Medal::getMedalType)
                    .orElse(null);

            boolean hasMedal = medalType != null;

            return ResultListItemDto.builder()
                    .id(result.getId())
                    .athleteId(athlete.getId())
                    .athleteName(athlete.getName())
                    .country(athlete.getCountry())
                    .sportId(sport.getId())
                    .sportName(sport.getName())
                    .value(result.getValue())
                    .status(result.getStatus())
                    .medalType(medalType)
                    .hasMedal(hasMedal)
                    .build();
        });
    }

    @PostMapping
    public ResponseEntity<Result> createResult(
            @RequestBody CreateResultRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        Athlete athlete = athleteRepository.findById(request.getAthleteId())
                .orElseThrow(() -> new IllegalArgumentException("Athlete not found"));

        Sport sport = sportRepository.findById(request.getSportId())
                .orElseThrow(() -> new IllegalArgumentException("Sport not found"));

        User creator = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new IllegalStateException("Current user not found"));

        Result result = new Result();
        result.setAthlete(athlete);
        result.setSport(sport);
        result.setCreatedBy(creator);
        result.setStatus(ResultStatus.PENDING);
        result.setValue(request.getValue());
        result.setTimestamp(OffsetDateTime.now().toInstant());
        result.setActive(true);

        Result saved = resultRepository.save(result);

        return ResponseEntity
                .created(URI.create("/api/results/" + saved.getId()))
                .body(saved);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Result> approveResult(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Optional<Result> resultOpt = resultRepository.findById(id);
        if (resultOpt.isEmpty()) return ResponseEntity.notFound().build();
        Result result = resultOpt.get();

        if (result.getStatus() != ResultStatus.PENDING) {
            return ResponseEntity.badRequest().header("X-Error", "Result is not PENDING").build();
        }
        String username = authentication.getName();
        User approver = userRepository.findByUsername(username).orElse(null);
        if (approver == null || !approver.isActive()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if (result.getCreatedBy().getId().equals(approver.getId())) {
            return ResponseEntity.badRequest().header("X-Error", "Approver equals creator").build();
        }

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

        if (admin == null || !admin.isActive() || admin.getRole() != UserRole.ADMIN)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Optional<Result> resultOpt = resultRepository.findById(id);
        if (resultOpt.isEmpty()) return ResponseEntity.notFound().build();
        Result result = resultOpt.get();

        result.setActive(false);
        result.setStatus(ResultStatus.REJECTED);

        Result saved = resultRepository.save(result);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Result> rejectResult(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Optional<Result> resultOpt = resultRepository.findById(id);
        if (resultOpt.isEmpty()) return ResponseEntity.notFound().build();
        Result result = resultOpt.get();

        if (result.getStatus() != ResultStatus.PENDING) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User reviewer = userRepository.findByUsername(authentication.getName()).orElse(null);
        if (reviewer == null || !reviewer.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (result.getCreatedBy().getId().equals(reviewer.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        result.setApprovedBy(reviewer);
        result.setStatus(ResultStatus.REJECTED);
        result.setActive(true);

        return ResponseEntity.ok(resultRepository.save(result));
    }

}
