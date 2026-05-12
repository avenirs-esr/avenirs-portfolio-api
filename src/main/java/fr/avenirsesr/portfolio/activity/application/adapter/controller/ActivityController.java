package fr.avenirsesr.portfolio.activity.application.adapter.controller;

import static fr.avenirsesr.portfolio.shared.application.adapter.Utils.extractOrigin;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.*;
import fr.avenirsesr.portfolio.activity.application.adapter.mapper.ActivityDetailsDtoMapper;
import fr.avenirsesr.portfolio.activity.application.adapter.mapper.ActivityNavigationMapper;
import fr.avenirsesr.portfolio.activity.application.adapter.mapper.ActivityOverviewDtoMapper;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityDetailData;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityWithStudentStatusData;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/activities")
public class ActivityController {
  private final ActivityService activityService;
  private final ActivityDetailsDtoMapper activityDetailsDtoMapper;
  private final ActivityNavigationMapper activityNavigationMapper;
  private final ActivityOverviewDtoMapper activityOverviewDtoMapper;

  @GetMapping("/{activityId}")
  public ResponseEntity<ActivityDetailsDTO> getActivityDetail(
      HttpServletRequest request, @PathVariable UUID activityId) {
    log.debug("Received request to get activity [{}] detail", activityId);

    ActivityDetailData activityDetail = activityService.getActivityDetail(activityId);
    String baseUrl = extractOrigin(request);
    return ResponseEntity.ok(activityDetailsDtoMapper.toDTO(activityDetail, baseUrl));
  }

  @GetMapping
  public ResponseEntity<PagedResponse<ActivityOverviewDTO>> getActivitiesView(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) EActivityThematic thematic) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to activities view of user [{}] (page= {}, fileSize= {})",
        principal.getName(),
        pageCriteria.page(),
        pageCriteria.pageSize());

    PagedResult<ActivityWithStudentStatusData> pagedResult =
        activityService.activitiesView(thematic, pageCriteria);

    var viewResponse =
        new PagedResponse<>(
            pagedResult.content().stream().map(activityOverviewDtoMapper::toDTO).toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo()));

    return ResponseEntity.ok(viewResponse);
  }

  @GetMapping("/latest")
  public ResponseEntity<PagedResponse<ActivityOverviewDTO>> getLatestActivitiesView(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to latest activities view by user [{}] (page= {}, fileSize= {})",
        principal.getName(),
        pageCriteria.page(),
        pageCriteria.pageSize());

    PagedResult<ActivityWithStudentStatusData> pagedResult =
        activityService.latestActivitiesView(pageCriteria);

    var viewResponse =
        new PagedResponse<>(
            pagedResult.content().stream().map(activityOverviewDtoMapper::toDTO).toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo()));

    return ResponseEntity.ok(viewResponse);
  }

  @GetMapping("/navigation")
  public ResponseEntity<List<ActivityNavigationDTO>> getActivityNavigation() {
    return ResponseEntity.ok(
        activityNavigationMapper.toDTO(activityService.getActivityNavigation()));
  }

  @PostMapping("/draft")
  public ResponseEntity<ActivityDraftCreationResponse> createActivityDraft(
      Principal principal, @RequestBody ActivityDraftCreationRequest body) {
    log.debug(
        "Received request to create activity draft by user [{}] body : {}",
        principal.getName(),
        body);

    var draft = activityService.createActivityDraft(body.title());
    return ResponseEntity.ok(new ActivityDraftCreationResponse(draft.getId()));
  }

  @PatchMapping("/{activityStatus}/{activityId}")
  public ResponseEntity<ActivityDraftCreationResponse> updateActivity(
      Principal principal,
      @PathVariable @Schema(ref = "#/components/schemas/EActivityStatus")
          EActivityStatus activityStatus,
      @PathVariable UUID activityId,
      @RequestBody ActivityDraftUpdateRequest body) {
    log.debug(
        "Received request to update activity by user [{}] for activity {} body : {}",
        principal.getName(),
        activityId,
        body);

    var draft =
        activityService.updateActivity(
            activityStatus,
            activityId,
            body.title(),
            body.thematic(),
            body.summary(),
            body.description(),
            body.executionPeriodInfo(),
            body.executionPeriodInfoSummary(),
            body.traceAllowedAssociations(),
            body.feedbackAllowedIterations(),
            body.enableReflection());
    return ResponseEntity.ok(new ActivityDraftCreationResponse(draft.getId()));
  }
}
