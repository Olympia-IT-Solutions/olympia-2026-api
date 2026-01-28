package de.solutions.it.olympia.repository;

import de.solutions.it.olympia.model.Medal;
import de.solutions.it.olympia.model.MedalType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedalRepository extends JpaRepository<Medal, Long> {

    List<Medal> findByActiveTrue();

    // Medaillen eines Landes (Country kommt aus Result -> Athlete)
    List<Medal> findByActiveTrueAndResult_Athlete_Country(String country);

    // Für Medaillenspiegel: nur Gold/Silber/Bronze
    List<Medal> findByActiveTrueAndMedalTypeIn(List<MedalType> medalTypes);

    // Upsert/Lookup für ein Ergebnis
    Optional<Medal> findByResult_IdAndActiveTrue(Long resultId);
}
