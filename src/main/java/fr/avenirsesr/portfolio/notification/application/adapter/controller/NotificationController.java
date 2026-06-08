package fr.avenirsesr.portfolio.notification.application.adapter.controller;

import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import java.security.Principal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/me/notifications")
public class NotificationController {

  private final NotificationService notificationService;

  @PatchMapping("/{id}/seen")
  public ResponseEntity<Void> markAsSeen(Principal principal, @PathVariable UUID id) {
    log.debug(
        "Received request to mark notification [{}] as seen by user [{}]", id, principal.getName());
    notificationService.markAsSeen(id);
    return ResponseEntity.noContent().build();
  }
}
