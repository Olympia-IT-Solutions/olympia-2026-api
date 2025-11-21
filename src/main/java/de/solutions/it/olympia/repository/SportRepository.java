package de.solutions.it.olympia.repository;

import de.solutions.it.olympia.model.Sport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SportRepository extends JpaRepository<Sport, Long> {
}
