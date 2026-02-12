package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.controller;

import fr.avenirsesr.portfolio.student.progress.imported.domain.port.input.ActivityProgressService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/me/activity-progress")
public class ActivityProgressController {
  private final ActivityProgressService activityProgressService;

  @PostMapping("/subscribe/{activityId}")
  public ResponseEntity<String> subscribe(@Valid @PathVariable UUID activityId) {
      activityProgressService.subscribe(activityId);
    return ResponseEntity.ok("Successfully subscribed to activity");
  }
}
