package de.solutions.it.olympia.controller;

import de.solutions.it.olympia.dto.ApproveResultRequest;
import de.solutions.it.olympia.dto.CreateResultRequest;
import de.solutions.it.olympia.model.*;
import de.solutions.it.olympia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/by-sport/{sportId}")
    public List<Result> getResultsBySport(@PathVariable Long sportId) {
        return resultRepository.findBySportIdAndStatusAndActiveTrue(
                sportId,
                ResultStatus.APPROVED
        );
    }

    @PostMapping
    public ResponseEntity<Result> createResult(@RequestBody CreateResultRequest request){

        Optional<User> userOptional = userRepository.findById(request.getUserId());
        if(userOptional.isEmpty() || !userOptional.get().isActive()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        User user = userOptional.get();

        Optional<Sport> sportOptional = sportRepository.findById(request.getSportId());
        if(sportOptional.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        Sport sport = sportOptional.get();

        Optional<Athlete> athleteOptional = athleteRepository.findById(request.getAthleteId());
        if(athleteOptional.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        Athlete athlete = athleteOptional.get();

        if(!athlete.getSport().getId().equals(sport.getId())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        if(request.getValue() == null || request.getValue().isBlank()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

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
            @RequestBody ApproveResultRequest request
    ) {
        Optional<Result> resultOpt = resultRepository.findById(id);
        if (resultOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Result result = resultOpt.get();

        if (result.getStatus() != ResultStatus.PENDING) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();


        Optional<User> approverOpt = userRepository.findById(request.getApproverUserId());
        if (approverOpt.isEmpty() || !approverOpt.get().isActive())  return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        User approver = approverOpt.get();

        if (result.getCreatedBy().getId().equals(approver.getId())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        result.setApprovedBy(approver);
        result.setStatus(ResultStatus.APPROVED);

        Result saved = resultRepository.save(result);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{id}/invalidate")
    public ResponseEntity<Result> invalidateResult(
            @PathVariable Long id,
            @RequestParam Long adminUserId
    ) {
        Optional<User> adminOpt = userRepository.findById(adminUserId);
        if (adminOpt.isEmpty() || !adminOpt.get().isActive() || !adminOpt.get().isAdmin()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Optional<Result> resultOpt = resultRepository.findById(id);
        if (resultOpt.isEmpty()) return ResponseEntity.notFound().build();
        Result result = resultOpt.get();

        result.setActive(false);
        result.setStatus(ResultStatus.REJECTED);

        Result saved = resultRepository.save(result);
        return ResponseEntity.ok(saved);
    }

}
