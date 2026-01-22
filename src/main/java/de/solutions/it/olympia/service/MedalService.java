package de.solutions.it.olympia.service;

import de.solutions.it.olympia.dto.AthleteMedalView;
import de.solutions.it.olympia.dto.CountryMedalSummary;
import de.solutions.it.olympia.model.Medal;
import de.solutions.it.olympia.model.MedalType;
import de.solutions.it.olympia.repository.MedalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public final class MedalService {

    private final MedalRepository medalRepository;

    public List<CountryMedalSummary> getCountryMedalTable() {
        List<Medal> medals = medalRepository.findByActiveTrueAndMedalTypeIn(
                List.of(MedalType.GOLD, MedalType.SILVER, MedalType.BRONZE)
        );

        Map<String, CountryMedalSummary.CountryMedalSummaryBuilder> map = new HashMap<>();

        for (Medal medal : medals) {
            String country = medal.getAthlete().getCountry();
            map.putIfAbsent(country, CountryMedalSummary.builder().country(country));

            var builder = map.get(country);

            switch (medal.getMedalType()) {
                case GOLD -> builder.gold(builder.build().getGold() + 1);
                case SILVER -> builder.silver(builder.build().getSilver() + 1);
                case BRONZE -> builder.bronze(builder.build().getBronze() + 1);
            }
        }

        return map.values().stream()
                .map(CountryMedalSummary.CountryMedalSummaryBuilder::build)
                .sorted(Comparator
                        .comparingLong(CountryMedalSummary::getGold).reversed()
                        .thenComparingLong(CountryMedalSummary::getSilver).reversed()
                        .thenComparingLong(CountryMedalSummary::getBronze).reversed()
                        .thenComparing(CountryMedalSummary::getCountry))
                .collect(Collectors.toList());
    }

    public List<AthleteMedalView> getMedalsByCountry(String country) {
        List<Medal> medals = medalRepository.findByActiveTrueAndAthlete_Country(country);

        return medals.stream()
                .map(medal -> AthleteMedalView.builder()
                        .athleteId(medal.getAthlete().getId())
                        .athleteName(medal.getAthlete().getName())
                        .sportName(medal.getAthlete().getSport().getName())
                        .medalType(medal.getMedalType())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
