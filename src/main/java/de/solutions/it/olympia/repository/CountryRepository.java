package de.solutions.it.olympia.repository;

import de.solutions.it.olympia.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    List<Country> findByActiveTrueOrderByNameAsc();
    Optional<Country> findByCode(String code);
}