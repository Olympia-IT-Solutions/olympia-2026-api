package de.solutions.it.olympia.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class CountryMedalSummary {

    String country;
    long gold;
    long silver;
    long bronze;

    public long getTotal() {
        return gold + silver + bronze;
    }
}
