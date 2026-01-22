package de.solutions.it.olympia.repository;

import de.solutions.it.olympia.model.Medal;
import de.solutions.it.olympia.model.MedalType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedalRepository extends JpaRepository<Medal, Long> {

    List<Medal> findByActiveTrue();

    List<Medal> findByActiveTrueAndAthlete_Country(String country);

    List<Medal> findByActiveTrueAndMedalTypeIn(List<MedalType> medalTypes);
}
