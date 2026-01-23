package de.solutions.it.olympia.dto;

import de.solutions.it.olympia.model.MedalType;
import de.solutions.it.olympia.model.ResultStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ResultListItemDto {

    Long id;

    Long athleteId;
    String athleteName;
    String country;

    Long sportId;
    String sportName;

    String value;
    ResultStatus status;

    MedalType medalType;
    boolean hasMedal;
}
