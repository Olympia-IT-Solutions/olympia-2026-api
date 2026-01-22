package de.solutions.it.olympia.dto;

import de.solutions.it.olympia.model.MedalType;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class AthleteMedalView {

    Long athleteId;
    String athleteName;
    String sportName;
    MedalType medalType;
}
