package com.example.proposalmanagement.auth.infrastructure.http;

import com.example.proposalmanagement.auth.infrastructure.persistence.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
public class Controller {

    @GetMapping("/")
    public String hello(@AuthenticationPrincipal UserDetails user) {
        return "Hello World: " + user.getUsername() + "!";
    }

    @GetMapping("/influencer")
    @PreAuthorize("hasRole('INFLUENCER')")
    public String influenceEndpoint() {
        return "Hello influencer!";
    }

    @GetMapping("/brand")
    @PreAuthorize("hasRole('BRAND')")
    public String brandEndpoint() {
        return "Hello brand!";
    }

    @GetMapping("/api/auth/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Não autenticado"));
        }
        return ResponseEntity.ok(new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().name()
        ));
    }

    public record UserProfileResponse(UUID id, String username, String role) {}
}
