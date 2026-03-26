package de.solutions.it.olympia.controller;

import de.solutions.it.olympia.dto.AuthRequestDto;
import de.solutions.it.olympia.dto.AuthResponseDto;
import de.solutions.it.olympia.model.User;
import de.solutions.it.olympia.repository.UserRepository;
import de.solutions.it.olympia.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

        return ResponseEntity.ok(
                AuthResponseDto.builder()
                        .token(token)
                        .username(user.getUsername())
                        .role(user.getRole())
                        .build()
        );
    }
}