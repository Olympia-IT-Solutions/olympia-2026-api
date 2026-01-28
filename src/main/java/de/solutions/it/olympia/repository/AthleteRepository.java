package de.solutions.it.olympia.repository;

import de.solutions.it.olympia.model.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AthleteRepository extends JpaRepository<Athlete, Long> {

    List<Athlete> findBySportIdAndActiveTrue(Long sportId);

    Optional<Athlete> findByName(String name);

}
