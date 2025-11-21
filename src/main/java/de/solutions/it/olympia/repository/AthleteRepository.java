package de.solutions.it.olympia.repository;

import de.solutions.it.olympia.model.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AthleteRepository extends JpaRepository<Athlete, Long> {

    List<Athlete> findBySportIdAndActiveTrue(Long sportId);
}
