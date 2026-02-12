package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.controller;

import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/me/activity-progress")
public class DeclaredActivityController {
  private final DeclaredActivityService declaredActivityService;

  @PostMapping("/subscribe/{activityId}")
  public ResponseEntity<DeclaredActivity> subscribe(@Valid @PathVariable UUID activityId) {
    DeclaredActivity declaredActivity = declaredActivityService.subscribe(activityId);
    return ResponseEntity.ok(declaredActivity);
  }
}
