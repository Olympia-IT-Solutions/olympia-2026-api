package de.solutions.it.olympia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "medals")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Medal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ direkte Referenz auf das freigegebene Ergebnis
    @OneToOne(optional = false)
    @JoinColumn(name = "result_id", nullable = false, unique = true)
    private Result result;

    @Enumerated(EnumType.STRING)
    @Column(name = "ergebnis", nullable = false)
    private MedalType medalType;

    @Column(name = "datum", nullable = false)
    private LocalDate date;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}
