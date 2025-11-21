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

    // Einzelperson oder Teamname
    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sportart_id")
    private Sport sport;

    @Column(nullable = false)
    private String country;   // z.B. "GER", "USA"

    @Column(nullable = false)
    private boolean active = true;
}
