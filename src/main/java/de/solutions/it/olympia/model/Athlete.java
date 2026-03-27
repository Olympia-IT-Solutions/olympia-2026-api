package de.solutions.it.olympia.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "athlete")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Athlete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sportart_id")
    private Sport sport;

    @ManyToOne(optional = false)
    @JoinColumn(name = "country_id")
    private Country country;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}