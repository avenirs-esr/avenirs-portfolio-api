package fr.avenirsesr.portfolio.shared.application.adapter.controller;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/seeding")
@RequiredArgsConstructor
public class SeedingAdminController {

  private final SeederOrchestrator seederOrchestrator;

  @Value("${admin.token:}")
  private String adminToken;

  @PostMapping("/run")
  public ResponseEntity<String> run(
      @RequestHeader(name = "X-ADMIN-TOKEN", required = false) String token) {
    if (adminToken == null || adminToken.isBlank()) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    if (token == null || !token.equals(adminToken)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    try {
      seederOrchestrator.resetAndSeed();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to clear existing data: " + e.getMessage());
    }
    return ResponseEntity.ok("Seeding is completed.");
  }
}
