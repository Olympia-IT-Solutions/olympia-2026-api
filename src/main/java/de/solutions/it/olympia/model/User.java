package de.solutions.it.olympia.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Anzeigename
    @Column(nullable = false, length = 100)
    private String name;

    // Login-Name (kann z.B. Mail-Adresse sein)
    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    // isAdmin im Pflichtenheft
    @Column(nullable = false)
    private boolean admin;

    @Column(nullable = false)
    private boolean active = true;
}
