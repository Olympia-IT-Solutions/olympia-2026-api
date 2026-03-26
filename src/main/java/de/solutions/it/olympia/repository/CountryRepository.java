package de.solutions.it.olympia.repository;

import de.solutions.it.olympia.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, String> {
    List<Country> findByActiveTrueOrderByNameAsc();
}