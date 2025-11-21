package de.solutions.it.olympia.repository;

import de.solutions.it.olympia.model.Result;
import de.solutions.it.olympia.model.ResultStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {

    List<Result> findBySportIdAndStatusAndActiveTrue(Long sportId, ResultStatus status);
}
