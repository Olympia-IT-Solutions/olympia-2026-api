package de.solutions.it.olympia.controller;

import de.solutions.it.olympia.dto.CreateResultRequest;
import de.solutions.it.olympia.dto.ResultListItemDto;
import de.solutions.it.olympia.dto.UpdateResultRequest;
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
import java.time.LocalDate;
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
            @RequestParam(required = false) Long countryId,
            @PageableDefault(
                    size = 50,
                    sort = "value",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {
        Page<Result> page;

        if (countryId != null) {
            page = resultRepository.findBySport_IdAndAthlete_Country_IdAndActiveTrue(sportId, countryId, pageable);
        } else {
            page = resultRepository.findBySport_IdAndActiveTrue(sportId, pageable);
        }

        return page.map(result -> {
            Athlete athlete = result.getAthlete();
            Sport sport = result.getSport();

            var medalOpt = medalRepository.findByResult_IdAndActiveTrue(result.getId());

            MedalType medalType = medalOpt
                    .map(Medal::getMedalType)
                    .orElse(null);

            boolean hasMedal = medalType != null;

            return ResultListItemDto.builder()
                    .id(result.getId())
                    .athleteId(athlete.getId())
                    .athleteName(athlete.getName())
                    .countryId(athlete.getCountry() != null ? athlete.getCountry().getId() : null)
                    .countryCode(athlete.getCountry() != null ? athlete.getCountry().getCode() : null)
                    .countryName(athlete.getCountry() != null ? athlete.getCountry().getName() : null)
                    .sportId(sport.getId())
                    .sportName(sport.getName())
                    .value(result.getValue())
                    .rank(result.getRank())
                    .status(result.getStatus())
                    .createdById(result.getCreatedBy() != null ? result.getCreatedBy().getId() : null)
                    .createdByUsername(result.getCreatedBy() != null ? result.getCreatedBy().getUsername() : null)
                    .approvedById(result.getApprovedBy() != null ? result.getApprovedBy().getId() : null)
                    .approvedByUsername(result.getApprovedBy() != null ? result.getApprovedBy().getUsername() : null)
                    .medalType(medalType)
                    .hasMedal(hasMedal)
                    .build();
        });
    }

    @PostMapping
    public ResponseEntity<Result> createResult(
            @RequestBody CreateResultRequest request,
            Authentication authentication
    ) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Athlete athlete = athleteRepository.findById(request.getAthleteId())
                .orElseThrow(() -> new IllegalArgumentException("Athlete not found"));

        Sport sport = sportRepository.findById(request.getSportId())
                .orElseThrow(() -> new IllegalArgumentException("Sport not found"));

        User creator = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Current user not found"));

        Result result = new Result();
        result.setAthlete(athlete);
        result.setSport(sport);
        result.setCreatedBy(creator);
        result.setStatus(ResultStatus.PENDING);
        result.setValue(request.getValue());
        result.setRank(request.getRank());
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
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Result not found"));

        if (result.getStatus() != ResultStatus.PENDING) {
            return ResponseEntity.badRequest().build();
        }

        String username = authentication.getName();
        User approver = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!approver.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (result.getCreatedBy().getId().equals(approver.getId())) {
            return ResponseEntity.badRequest().build();
        }

        result.setApprovedBy(approver);
        result.setStatus(ResultStatus.APPROVED);

        Result saved = resultRepository.save(result);

        // ✅ MEDAILLE AUTOMATISCH ERSTELLEN
        createOrUpdateMedalForResult(saved);

        return ResponseEntity.ok(saved);
    }


    @PostMapping("/{id}/invalidate")
    public ResponseEntity<Result> invalidateResult(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String username = authentication.getName();
        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!admin.isActive() || admin.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Result not found"));

        result.setActive(false);
        result.setStatus(ResultStatus.REJECTED);

        Result saved = resultRepository.save(result);

        // ❌ MEDAILLE DEAKTIVIEREN
        medalRepository.findByResult_IdAndActiveTrue(result.getId())
                .ifPresent(medal -> {
                    medal.setActive(false);
                    medalRepository.save(medal);
                });

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

    private void createOrUpdateMedalForResult(Result result) {

        if (result.getRank() == null) return;

        MedalType type = switch (result.getRank()) {
            case 1 -> MedalType.GOLD;
            case 2 -> MedalType.SILVER;
            case 3 -> MedalType.BRONZE;
            default -> null;
        };

        if (type == null) return;

        medalRepository.findByResult_IdAndActiveTrue(result.getId())
                .ifPresentOrElse(
                        medal -> {
                            medal.setMedalType(type);
                            medalRepository.save(medal);
                        },
                        () -> {
                            Medal medal = new Medal();
                            medal.setResult(result);
                            medal.setMedalType(type);
                            medal.setDate(java.time.LocalDate.now());
                            medal.setActive(true);
                            medalRepository.save(medal);
                        }
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result> updateResult(
            @PathVariable Long id,
            @RequestBody UpdateResultRequest request,
            Authentication authentication
    ) {
        Result result = resultRepository.findById(id).orElse(null);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        if (result.getStatus() != ResultStatus.PENDING) {
            return ResponseEntity.badRequest().build();
        }

        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);

        if (currentUser == null || !currentUser.isActive()) {
            return ResponseEntity.status(403).build();
        }

        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        boolean isCreator = result.getCreatedBy().getId().equals(currentUser.getId());

        if (!isAdmin && !isCreator) {
            return ResponseEntity.status(403).build();
        }

        if (request.getValue() != null) {
            result.setValue(request.getValue());
        }

        if (request.getRank() != null) {
            result.setRank(request.getRank());
        }

        Result saved = resultRepository.save(result);
        return ResponseEntity.ok(saved);
    }

}
