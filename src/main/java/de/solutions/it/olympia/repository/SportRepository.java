package de.solutions.it.olympia.repository;

import de.solutions.it.olympia.model.Sport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SportRepository extends JpaRepository<Sport, Long> {

    Optional<Sport> findByName(String name);

}
