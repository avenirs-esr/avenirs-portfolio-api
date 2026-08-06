package fr.avenirsesr.portfolio.notification.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.notification.application.adapter.dto.NotificationDTO;
import fr.avenirsesr.portfolio.notification.application.adapter.mapper.NotificationResponseMapper;
import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import java.security.Principal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/me/notifications")
public class NotificationController {

  private final NotificationService notificationService;

  @PreAuthorize("hasAuthority('notification:read:own')")
  @GetMapping("/{userCategory}")
  public ResponseEntity<PagedResponse<NotificationDTO>> getNotifications(
      Principal principal,
      @Parameter(
              in = ParameterIn.PATH,
              schema = @Schema(ref = "#/components/schemas/EUserCategory"))
          @PathVariable
          EUserCategory userCategory,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to get notifications for user [{}] (userCategory={}, page={},"
            + " pageSize={})",
        principal.getName(),
        userCategory,
        pageCriteria.page(),
        pageCriteria.pageSize());
    var result = notificationService.getNotifications(userCategory, pageCriteria);
    return ResponseEntity.ok(
        new PagedResponse<>(
            result.content().stream().map(NotificationResponseMapper.INSTANCE::toDTO).toList(),
            PageInfoDTO.fromDomain(result.pageInfo())));
  }

  @PreAuthorize("hasAuthority('notification:update:own')")
  @PatchMapping("/{id}/seen")
  public ResponseEntity<Void> markAsSeen(Principal principal, @PathVariable UUID id) {
    log.debug(
        "Received request to mark notification [{}] as seen by user [{}]", id, principal.getName());
    notificationService.markAsSeen(id);
    return ResponseEntity.noContent().build();
  }
}
