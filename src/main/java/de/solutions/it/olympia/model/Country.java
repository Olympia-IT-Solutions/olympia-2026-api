package de.solutions.it.olympia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "countries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Country {

    @Id
    @Column(length = 3, nullable = false)
    private String code;   // GER, USA, SWE ...

    @Column(nullable = false)
    private String name;   // Germany, United States ...

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}