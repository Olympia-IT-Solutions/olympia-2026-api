package de.solutions.it.olympia.repository;

import de.solutions.it.olympia.model.Medal;
import de.solutions.it.olympia.model.MedalType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedalRepository extends JpaRepository<Medal, Long> {

    List<Medal> findByActiveTrue();

    List<Medal> findByActiveTrueAndAthlete_Country(String country);

    List<Medal> findByActiveTrueAndMedalTypeIn(List<MedalType> medalTypes);

    Optional<Medal> findFirstByAthlete_IdAndAthlete_Sport_IdAndActiveTrue(Long athleteId, Long sportId);

}
