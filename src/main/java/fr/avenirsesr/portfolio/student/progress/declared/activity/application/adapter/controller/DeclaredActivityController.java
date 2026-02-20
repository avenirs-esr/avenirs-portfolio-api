package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityViewDtoMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/me/activity-progress")
public class DeclaredActivityController {
  private final DeclaredActivityService declaredActivityService;

  @GetMapping
  public ResponseEntity<PagedResponse<DeclaredActivityViewDTO>> getDeclaredActivitiesView(
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);

    PagedResult<DeclaredActivity> pagedResult =
        declaredActivityService.getDeclaredActivities(pageCriteria);

    var viewResponse =
        new PagedResponse<>(
            pagedResult.content().stream().map(DeclaredActivityViewDtoMapper::toDTO).toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo()));

    return ResponseEntity.ok(viewResponse);
  }

  @PostMapping("/subscribe/{activityId}")
  public ResponseEntity<DeclaredActivity> subscribe(@Valid @PathVariable UUID activityId) {
    DeclaredActivity declaredActivity = declaredActivityService.subscribe(activityId);
    return ResponseEntity.ok(declaredActivity);
  }

  @DeleteMapping("/unsubscribe")
  public ResponseEntity<String> unsubscribe(@RequestBody List<UUID> declaredActivityIds) {

    declaredActivityService.unsubscribeMultiple(declaredActivityIds);

    return ResponseEntity.ok("Declared activities successfully unsubscribed");
  }
}
