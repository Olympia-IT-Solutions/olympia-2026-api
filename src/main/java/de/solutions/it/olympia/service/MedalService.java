package de.solutions.it.olympia.service;

import de.solutions.it.olympia.dto.AthleteMedalView;
import de.solutions.it.olympia.dto.CountryMedalSummary;
import de.solutions.it.olympia.model.Athlete;
import de.solutions.it.olympia.model.Medal;
import de.solutions.it.olympia.model.MedalType;
import de.solutions.it.olympia.model.Result;
import de.solutions.it.olympia.repository.MedalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedalService {

    private final MedalRepository medalRepository;

    public List<CountryMedalSummary> getCountryMedalTable() {
        List<Medal> medals = medalRepository.findByActiveTrueAndMedalTypeIn(
                List.of(MedalType.GOLD, MedalType.SILVER, MedalType.BRONZE)
        );

        Map<String, long[]> counts = new HashMap<>();
        // counts[country] = [gold, silver, bronze]

        for (Medal medal : medals) {
            Result result = medal.getResult();
            Athlete athlete = result.getAthlete();
            String country = athlete.getCountry();

            counts.putIfAbsent(country, new long[]{0, 0, 0});

            switch (medal.getMedalType()) {
                case GOLD -> counts.get(country)[0]++;
                case SILVER -> counts.get(country)[1]++;
                case BRONZE -> counts.get(country)[2]++;
                default -> { /* ignore */ }
            }
        }

        return counts.entrySet().stream()
                .map(e -> CountryMedalSummary.builder()
                        .country(e.getKey())
                        .gold(e.getValue()[0])
                        .silver(e.getValue()[1])
                        .bronze(e.getValue()[2])
                        .build()
                )
                .sorted(Comparator
                        .comparingLong(CountryMedalSummary::getGold).reversed()
                        .thenComparingLong(CountryMedalSummary::getSilver).reversed()
                        .thenComparingLong(CountryMedalSummary::getBronze).reversed()
                        .thenComparing(CountryMedalSummary::getCountry)
                )
                .collect(Collectors.toList());
    }

    public List<AthleteMedalView> getMedalsByCountry(String country) {
        return medalRepository.findByActiveTrueAndResult_Athlete_Country(country).stream()
                .map(medal -> {
                    Result result = medal.getResult();
                    Athlete athlete = result.getAthlete();

                    return AthleteMedalView.builder()
                            .resultId(result.getId())
                            .athleteId(athlete.getId())
                            .athleteName(athlete.getName())
                            .sportName(result.getSport().getName())
                            .medalType(medal.getMedalType())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
