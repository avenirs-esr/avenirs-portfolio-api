package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.*;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityAssociationsDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityDetailsDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityViewDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/me/activity-progress")
public class DeclaredActivityController {
  private final DeclaredActivityService declaredActivityService;

  @GetMapping
  public ResponseEntity<PagedResponse<DeclaredActivityViewDTO>> getDeclaredActivitiesView(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to get declared activities view of user [{}] (page= {}, size= {})",
        principal.getName(),
        pageCriteria.page(),
        pageCriteria.pageSize());
    PagedResult<DeclaredActivity> pagedResult =
        declaredActivityService.getDeclaredActivities(pageCriteria);

    var viewResponse =
        new PagedResponse<>(
            pagedResult.content().stream().map(DeclaredActivityViewDTOMapper::toDTO).toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo()));

    return ResponseEntity.ok(viewResponse);
  }

  @PostMapping("/subscribe/{activityId}")
  public ResponseEntity<DeclaredActivityDetailsDTO> subscribeActivity(
      @Valid @PathVariable UUID activityId,
      @Valid @RequestBody SubscribeDeclaredActivityRequest subscribeDeclaredActivityRequest) {
    LocalDate startDate =
        subscribeDeclaredActivityRequest.period() != null
            ? subscribeDeclaredActivityRequest.period().startDate()
            : null;
    LocalDate endDate =
        subscribeDeclaredActivityRequest.period() != null
            ? subscribeDeclaredActivityRequest.period().endDate()
            : null;
    DeclaredActivity declaredActivity =
        declaredActivityService.subscribe(activityId, startDate, endDate);
    return ResponseEntity.ok(DeclaredActivityDetailsDTOMapper.toDTO(declaredActivity));
  }

  @DeleteMapping("/unsubscribe")
  public ResponseEntity<String> unsubscribeActivitiesProgresses(
      @RequestBody List<UUID> activityIds) {

    declaredActivityService.unsubscribeMultiple(activityIds);

    return ResponseEntity.ok("Declared activities successfully unsubscribed");
  }

  @PutMapping("/finish/{declaredActivityId}")
  public ResponseEntity<DeclaredActivityDetailsDTO> finish(
      Principal principal, @Valid @PathVariable UUID declaredActivityId) {
    log.debug(
        "Received request to finish declared activity [{}] for student [{}]",
        declaredActivityId,
        principal.getName());
    DeclaredActivity declaredActivity = declaredActivityService.finish(declaredActivityId);
    return ResponseEntity.ok(DeclaredActivityDetailsDTOMapper.toDTO(declaredActivity));
  }

  @PutMapping("/{activityId}/reflection")
  public ResponseEntity<String> updateReflection(
      @PathVariable("activityId") UUID activityId,
      @Valid @RequestBody UpdateReflectionRequest request) {

    declaredActivityService.updateReflection(activityId, request.reflection());
    return ResponseEntity.ok("Declared activities successfully updated");
  }

  @GetMapping("/{declaredActivityId}")
  public ResponseEntity<DeclaredActivityDetailsDTO> getDeclaredActivityDetails(
      Principal principal, @Valid @PathVariable UUID declaredActivityId) {
    log.debug(
        "Received request to get declared activity [{}] details for student [{}]",
        declaredActivityId,
        principal.getName());
    return ResponseEntity.ok(
        DeclaredActivityDetailsDTOMapper.toDTO(
            declaredActivityService.getDeclaredActivityDetails(declaredActivityId)));
  }

  @GetMapping("/{declaredActivityId}/associations")
  public ResponseEntity<DeclaredActivityAssociationsDTO> getDeclaredActivityAssociations(
      Principal principal, @Valid @PathVariable UUID declaredActivityId) {
    log.debug(
        "Received request to get declared activity [{}] association for student [{}]",
        declaredActivityId,
        principal.getName());
    return ResponseEntity.ok(
        DeclaredActivityAssociationsDTOMapper.toDTO(
            declaredActivityService.getDeclaredActivityAssociations(declaredActivityId)));
  }

  @PutMapping("/{declaredActivityId}/period")
  public ResponseEntity<String> updatePeriod(
      @Valid @RequestBody DeclaredActivityPeriodRequest declaredActivityPeriodRequest,
      @PathVariable UUID declaredActivityId) {
    LocalDate startDate =
        declaredActivityPeriodRequest.period() != null
            ? declaredActivityPeriodRequest.period().startDate()
            : null;
    LocalDate endDate =
        declaredActivityPeriodRequest.period() != null
            ? declaredActivityPeriodRequest.period().endDate()
            : null;
    declaredActivityService.updateDeclaredActivityDates(declaredActivityId, startDate, endDate);
    return ResponseEntity.ok("Declared activities dates successfully updated");
  }

  @PostMapping("/{declaredActivityId}/associate/trace")
  public ResponseEntity<String> associateActivityWithTraces(
      Principal principal,
      @Valid @PathVariable UUID declaredActivityId,
      @Valid @RequestBody DeclaredActivityAssociationRequest body) {
    log.debug(
        "Received request to associate declared activity [{}] with traces [{}] by student [{}]",
        declaredActivityId,
        body.idsToAssociate(),
        principal.getName());
    declaredActivityService.associateActivityWithTraces(declaredActivityId, body.idsToAssociate());
    return ResponseEntity.ok("Declared activities associated successfully");
  }
}
