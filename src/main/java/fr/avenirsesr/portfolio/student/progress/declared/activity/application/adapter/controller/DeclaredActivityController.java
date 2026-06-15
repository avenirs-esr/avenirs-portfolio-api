package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.controller;

import fr.avenirsesr.portfolio.association.application.adapter.dto.AssociationSearchResultDeclaredActivityDTO;
import fr.avenirsesr.portfolio.association.application.adapter.dto.AssociationSearchResultTraceDTO;
import fr.avenirsesr.portfolio.association.application.adapter.mapper.AssociationSearchResultDTOMapper;
import fr.avenirsesr.portfolio.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.AssociationsCreationRequest;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.AssociationsDeleteRequest;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.CreationResponse;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityAssociationsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityPeriodRequest;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.SubscribeDeclaredActivityRequest;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.UpdateReflectionRequest;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityAssociationsDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityDetailsDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityViewDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/me/activity-progress")
public class DeclaredActivityController {
  private final DeclaredActivityService declaredActivityService;
  private final DeclaredActivityViewDTOMapper declaredActivityViewDTOMapper;
  private final DeclaredActivityDetailsDTOMapper declaredActivityDetailsDTOMapper;
  private final DeclaredActivityAssociationsDTOMapper declaredActivityAssociationsDTOMapper;
  private final AssociationSearchResultDTOMapper associationSearchResultDTOMapper;

  @GetMapping
  public ResponseEntity<PagedResponse<DeclaredActivityViewDTO>> getDeclaredActivitiesView(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);

    log.debug(
        "Received request to get declared activities view of user [{}] (page= {}, fileSize= {})",
        principal.getName(),
        pageCriteria.page(),
        pageCriteria.pageSize());

    PagedResult<DeclaredActivity> pagedResult =
        declaredActivityService.getDeclaredActivities(pageCriteria);

    var statusByDeclaredActivity =
        declaredActivityService.getDeclaredActivityStatus(pagedResult.content());

    var viewResponse =
        new PagedResponse<>(
            pagedResult.content().stream()
                .map(
                    declaredActivity ->
                        declaredActivityViewDTOMapper.toDTO(
                            declaredActivity, statusByDeclaredActivity.get(declaredActivity)))
                .toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo()));

    return ResponseEntity.ok(viewResponse);
  }

  @PostMapping("/subscribe/{activityId}")
  public ResponseEntity<CreationResponse> subscribeActivity(
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
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new CreationResponse(declaredActivity.getId()));
  }

  @DeleteMapping("/unsubscribe")
  public ResponseEntity<String> unsubscribeActivitiesProgresses(
      @RequestBody List<UUID> activityIds) {

    declaredActivityService.unsubscribeMultiple(activityIds);

    return ResponseEntity.ok("Declared activities successfully unsubscribed");
  }

  @PutMapping("/finish/{declaredActivityId}")
  public ResponseEntity<Void> finish(
      Principal principal, @Valid @PathVariable UUID declaredActivityId) {
    log.debug(
        "Received request to finish declared activity [{}] for student [{}]",
        declaredActivityId,
        principal.getName());
    declaredActivityService.finish(declaredActivityId);
    return ResponseEntity.noContent().build();
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
    var details = declaredActivityService.getDeclaredActivityDetails(declaredActivityId);
    var status = declaredActivityService.getDeclaredActivityStatus(details.declaredActivity());

    return ResponseEntity.ok(declaredActivityDetailsDTOMapper.toDTO(details, status));
  }

  @GetMapping("/{declaredActivityId}/associations")
  public ResponseEntity<DeclaredActivityAssociationsDTO> getDeclaredActivityAssociations(
      Principal principal, @Valid @PathVariable UUID declaredActivityId) {
    log.debug(
        "Received request to get declared activity [{}] associations for student [{}]",
        declaredActivityId,
        principal.getName());
    return ResponseEntity.ok(
        declaredActivityAssociationsDTOMapper.toDTO(
            declaredActivityService.getDeclaredActivityAssociations(declaredActivityId)));
  }

  @DeleteMapping("/{declaredActivityId}/associations")
  public ResponseEntity<String> deleteDeclaredActivityAssociations(
      Principal principal,
      @Valid @PathVariable UUID declaredActivityId,
      @Valid @RequestBody AssociationsDeleteRequest body) {
    log.debug(
        "Received request to delete declared activity [{}] associations for student [{}]",
        declaredActivityId,
        principal.getName());
    declaredActivityService.deleteAssociations(declaredActivityId, body.idsToDelete());
    return ResponseEntity.ok("Declared activities associations successfully deleted");
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

  @PostMapping("/{declaredActivityId}/associate/traces")
  public ResponseEntity<DeclaredActivityAssociationsDTO> associateActivityWithTraces(
      Principal principal,
      @Valid @PathVariable UUID declaredActivityId,
      @Valid @RequestBody AssociationsCreationRequest body) {
    log.debug(
        "Received request to associate declared activity [{}] with traces [{}] by student [{}]",
        declaredActivityId,
        body.idsToAssociate(),
        principal.getName());
    var newAssociations =
        declaredActivityService.associateActivityWithTraces(
            declaredActivityId, body.idsToAssociate());
    return ResponseEntity.ok(declaredActivityAssociationsDTOMapper.toDTO(newAssociations));
  }

  @PostMapping("/{declaredActivityId}/associate/declared-skills")
  public ResponseEntity<DeclaredActivityAssociationsDTO> associateActivityWithDeclaredSkills(
      Principal principal,
      @Valid @PathVariable UUID declaredActivityId,
      @Valid @RequestBody AssociationsCreationRequest body) {
    log.debug(
        "Received request to associate declared activity [{}] with declared skills [{}] by student"
            + " [{}]",
        declaredActivityId,
        body.idsToAssociate(),
        principal.getName());
    var newAssociations =
        declaredActivityService.associateActivityWithDeclaredSkills(
            declaredActivityId, body.idsToAssociate());
    return ResponseEntity.ok(declaredActivityAssociationsDTOMapper.toDTO(newAssociations));
  }

  @GetMapping("/search-for-association")
  public ResponseEntity<PagedResponse<AssociationSearchResultDeclaredActivityDTO>>
      searchDeclaredActivitiesForAssociation(
          Principal principal,
          @RequestParam(required = false) UUID excludeAssociatedWithElementId,
          @Parameter(schema = @Schema(ref = "#/components/schemas/EAssociationContextType"))
              @RequestParam(required = false)
              EAssociationContextType contextType,
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) Integer page,
          @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to search declared activities for association (contextType={},"
            + " excludeAssociatedWithElementId={}) by student [{}] (keyword={}, page={},"
            + " pageSize={})",
        contextType,
        excludeAssociatedWithElementId,
        principal.getName(),
        keyword,
        pageCriteria.page(),
        pageCriteria.pageSize());

    PagedResult<AssociationSearchResultData> pagedResult =
        declaredActivityService.searchDeclaredActivitiesForAssociation(
            excludeAssociatedWithElementId, contextType, keyword, pageCriteria);

    return ResponseEntity.ok(
        new PagedResponse<>(
            pagedResult.content().stream()
                .map(associationSearchResultDTOMapper::toDeclaredActivityDTO)
                .toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo())));
  }

  @GetMapping("/{declaredActivityId}/search-for-association/traces")
  public ResponseEntity<PagedResponse<AssociationSearchResultTraceDTO>> searchTracesForAssociation(
      Principal principal,
      @Valid @PathVariable UUID declaredActivityId,
      @RequestParam(required = false) Boolean isAssociated,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to search traces for association with declared activity [{}] by student"
            + " [{}] (isAssociated={}, keyword={}, page={}, pageSize={})",
        declaredActivityId,
        principal.getName(),
        isAssociated,
        keyword,
        pageCriteria.page(),
        pageCriteria.pageSize());

    PagedResult<AssociationSearchResultData> pagedResult =
        declaredActivityService.searchTracesForAssociation(
            declaredActivityId, keyword, pageCriteria, isAssociated);

    var response =
        new PagedResponse<>(
            pagedResult.content().stream()
                .map(associationSearchResultDTOMapper::toTraceDTO)
                .toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo()));

    return ResponseEntity.ok(response);
  }
}
