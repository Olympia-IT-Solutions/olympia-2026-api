package de.solutions.it.olympia.controller;

import de.solutions.it.olympia.dto.CreateUserRequest;
import de.solutions.it.olympia.dto.UserListItemDto;
import de.solutions.it.olympia.model.User;
import de.solutions.it.olympia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<UserListItemDto> createUser(@RequestBody CreateUserRequest req) {
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.status(409).build();
        }

        User u = new User();
        u.setName(req.getName());
        u.setUsername(req.getUsername());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setRole(req.getRole());
        u.setActive(true);

        User saved = userRepository.save(u);
        return ResponseEntity.status(201).body(toDto(saved));
    }

    @GetMapping
    public List<UserListItemDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setActive(false);
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    private UserListItemDto toDto(User user) {
        return UserListItemDto.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .role(user.getRole())
                .active(user.isActive())
                .build();
    }
}