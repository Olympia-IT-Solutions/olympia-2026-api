package de.solutions.it.olympia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "medals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "athlete_id")
    private Athlete athlete;

    @Enumerated(EnumType.STRING)
    @Column(name = "ergebnis", nullable = false)
    private MedalType medalType;   // GOLD, SILVER, BRONZE, DISQUALIFIED

    @Column(name = "datum", nullable = false)
    private LocalDate date;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}
