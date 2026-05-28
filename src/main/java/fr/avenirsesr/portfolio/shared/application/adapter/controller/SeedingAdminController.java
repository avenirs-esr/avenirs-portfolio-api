package fr.avenirsesr.portfolio.shared.application.adapter.controller;

import fr.avenirsesr.portfolio.common.seeder.domain.model.enums.ESeedMode;
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

  @PostMapping("/{table}/run")
  public ResponseEntity<String> runTable(
      @PathVariable String table,
      @RequestParam(defaultValue = "false") boolean overwrite,
      @RequestHeader(name = "X-ADMIN-TOKEN", required = false) String token) {

    if (adminToken == null || adminToken.isBlank()) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    if (token == null || !token.equals(adminToken)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    try {
      var mode = overwrite ? ESeedMode.OVERWRITE : ESeedMode.INSERT_ONLY;
      int processed = seederOrchestrator.seedTable(table, mode);

      return ResponseEntity.ok(
          "Seeding completed for table: " + table + ". Rows processed: " + processed);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Seeding failed for table " + table + ": " + e.getMessage());
    }
  }
}
