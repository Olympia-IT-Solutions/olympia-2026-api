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

    Long countryId;
    String countryCode;
    String countryName;

    Long sportId;
    String sportName;

    String value;
    Integer rank;

    ResultStatus status;

    Long createdById;
    String createdByUsername;

    Long approvedById;
    String approvedByUsername;

    MedalType medalType;
    boolean hasMedal;
}