package de.solutions.it.olympia.repository;

import de.solutions.it.olympia.model.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository extends JpaRepository<Result, Long> {

    Page<Result> findBySport_IdAndActiveTrue(Long sportId, Pageable pageable);

    Page<Result> findBySport_IdAndAthlete_Country_CodeAndActiveTrue(
            Long sportId,
            String countryCode,
            Pageable pageable
    );
}