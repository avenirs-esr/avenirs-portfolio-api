package fr.avenirsesr.portfolio.activity.application.adapter.controller;

import static fr.avenirsesr.portfolio.shared.application.adapter.Utils.extractOrigin;
import static fr.avenirsesr.portfolio.shared.application.adapter.Utils.readBytes;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.*;
import fr.avenirsesr.portfolio.activity.application.adapter.mapper.*;
import fr.avenirsesr.portfolio.activity.application.adapter.request.ActivityDraftCreationRequest;
import fr.avenirsesr.portfolio.activity.application.adapter.request.ActivityDraftUpdateRequest;
import fr.avenirsesr.portfolio.activity.application.adapter.response.ActivityDraftCreationResponse;
import fr.avenirsesr.portfolio.activity.application.adapter.response.ActivityDraftUpdateResponse;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityPresentationData;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityStaffOverviewData;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityWithStudentStatusData;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.CreationResponse;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.shared.application.adapter.mapper.FileDTOMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/activities")
public class ActivityController {
  private final ActivityService activityService;
  private final ActivityPresentationDtoMapper activityPresentationDtoMapper;
  private final ActivityContentDtoMapper activityContentDtoMapper;
  private final ActivityNavigationMapper activityNavigationMapper;
  private final ActivityOverviewDtoMapper activityOverviewDtoMapper;
  private final ActivityStaffOverviewDtoMapper activityStaffOverviewDtoMapper;
  private final FileDTOMapper fileDTOMapper;

  @GetMapping("/{activityStatus}/{activityId}/presentation")
  public ResponseEntity<ActivityPresentationDTO> getActivityPresentation(
      HttpServletRequest request,
      @PathVariable
          @Parameter(
              name = "activityStatus",
              in = ParameterIn.PATH,
              required = true,
              schema = @Schema(ref = "#/components/schemas/EActivityStatus"))
          EActivityStatus activityStatus,
      @PathVariable UUID activityId) {
    log.debug("Received request to get activity [{}] presentation", activityId);

    String baseUrl = extractOrigin(request);
    ActivityPresentationData activityPresentation =
        activityService.getActivityPresentation(activityStatus, activityId);
    var dto = activityPresentationDtoMapper.toDTO(activityPresentation, baseUrl);
    return ResponseEntity.ok(dto);
  }

  @GetMapping("/{activityStatus}/{activityId}/content")
  public ResponseEntity<ActivityContentDTO> getActivityContent(
      HttpServletRequest request,
      @PathVariable
          @Parameter(
              name = "activityStatus",
              in = ParameterIn.PATH,
              required = true,
              schema = @Schema(ref = "#/components/schemas/EActivityStatus"))
          EActivityStatus activityStatus,
      @PathVariable UUID activityId) {
    log.debug("Received request to get activity [{}] content", activityId);

    String baseUrl = extractOrigin(request);
    ActivityContentDTO dto =
        switch (activityStatus) {
          case PUBLISHED, UNPUBLISHED -> {
            var activity = activityService.getActivityById(activityId);
            yield activityContentDtoMapper.toDTO(
                activity, fileDTOMapper.toFileDTOs(activity.getFiles(), baseUrl));
          }
          case DRAFT -> {
            var draft = activityService.getActivityDraftById(activityId);
            yield activityContentDtoMapper.toDTO(
                draft,
                activityService.hasEnrolledStudents(draft),
                fileDTOMapper.toFileDTOs(draft.getFiles(), baseUrl));
          }
        };
    return ResponseEntity.ok(dto);
  }

  @GetMapping("/staff/working-space")
  public ResponseEntity<PagedResponse<ActivityStaffOverviewDTO>> getStaffActivityWorkingSpace(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize,
      @Parameter(schema = @Schema(ref = "#/components/schemas/EActivityStatus"))
          @RequestParam(required = false)
          EActivityStatus status) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to get activity working space view of user [{}] (page= {}, fileSize= {})",
        principal.getName(),
        pageCriteria.page(),
        pageCriteria.pageSize());

    PagedResult<ActivityStaffOverviewData> pagedResult =
        activityService.staffActivityWorkingSpace(pageCriteria, status);

    var viewResponse =
        new PagedResponse<>(
            pagedResult.content().stream().map(activityStaffOverviewDtoMapper::toDTO).toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo()));

    return ResponseEntity.ok(viewResponse);
  }

  @GetMapping("/staff/library")
  public ResponseEntity<PagedResponse<ActivityStaffOverviewDTO>> getStaffActivityLibrary(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize,
      @Parameter(schema = @Schema(ref = "#/components/schemas/EActivityThematic"))
          @RequestParam(required = false)
          EActivityThematic thematic) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to get activity library view of user [{}] (page= {}, fileSize= {})",
        principal.getName(),
        pageCriteria.page(),
        pageCriteria.pageSize());

    PagedResult<ActivityStaffOverviewData> pagedResult =
        activityService.staffActivityLibrary(thematic, pageCriteria);

    var viewResponse =
        new PagedResponse<>(
            pagedResult.content().stream().map(activityStaffOverviewDtoMapper::toDTO).toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo()));

    return ResponseEntity.ok(viewResponse);
  }

  @GetMapping
  public ResponseEntity<PagedResponse<ActivityOverviewDTO>> getActivitiesView(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize,
      @Parameter(schema = @Schema(ref = "#/components/schemas/EActivityThematic"))
          @RequestParam(required = false)
          EActivityThematic thematic) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to activities view of user [{}] (page= {}, pageSize= {})",
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
        "Received request to latest activities view by user [{}] (page= {}, pageSize= {})",
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

  @PostMapping("/create-draft/{activityId}")
  public ResponseEntity<ActivityDraftCreationResponse> createDraftFromActivity(
      Principal principal, @PathVariable UUID activityId) {
    log.debug(
        "Received request to create draft from activity [{}] by user [{}]",
        activityId,
        principal.getName());

    var draft = activityService.createDraftFromActivity(activityId);
    return ResponseEntity.ok(new ActivityDraftCreationResponse(draft.getId()));
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

  @PostMapping("/publish/{activityDraftId}")
  public ResponseEntity<CreationResponse> publishActivityDraft(
      Principal principal, @PathVariable UUID activityDraftId) {
    log.debug(
        "Received request to publish activity draft {} by user [{}]",
        activityDraftId,
        principal.getName());

    var activity = activityService.publish(activityDraftId);
    return ResponseEntity.ok(new CreationResponse(activity.getId()));
  }

  @PostMapping("/unpublish/{activityId}")
  public ResponseEntity<Void> unpublishActivity(
      Principal principal, @PathVariable UUID activityId) {
    log.debug(
        "Received request to unpublish activity {} by user [{}]", activityId, principal.getName());

    activityService.unpublish(activityId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/draft/{activityDraftId}")
  public ResponseEntity<String> deleteActivityDraft(
      Principal principal, @PathVariable UUID activityDraftId) {
    log.debug(
        "Received request to delete activity draft {} by user [{}]",
        activityDraftId,
        principal.getName());

    activityService.deleteDraft(activityDraftId);

    return ResponseEntity.ok("Activity draft successfully deleted");
  }

  @PatchMapping("/{activityDraftId}")
  public ResponseEntity<ActivityDraftUpdateResponse> updateActivityDraft(
      Principal principal,
      @PathVariable UUID activityDraftId,
      @RequestBody ActivityDraftUpdateRequest body) {
    log.debug(
        "Received request to update activity draft by user [{}] for draft {} body : {}",
        principal.getName(),
        activityDraftId,
        body);

    var draft =
        activityService.updateActivityDraft(
            activityDraftId,
            body.title(),
            body.thematic(),
            body.summary(),
            body.description(),
            body.recommendedCompletionContexts(),
            body.startDate(),
            body.endDate(),
            body.traceAllowedAssociations(),
            body.feedbackAllowedIterations(),
            body.enableReflection(),
            body.links());
    return ResponseEntity.ok(new ActivityDraftUpdateResponse(draft.getId()));
  }

  @PostMapping(
      value = "/draft/{activityDraftId}/banner",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<FileDTO> uploadDraftBanner(
      HttpServletRequest request,
      Principal principal,
      @PathVariable UUID activityDraftId,
      @RequestParam("file") MultipartFile file) {
    log.debug(
        "Received request to upload banner for activity draft [{}] by user [{}]",
        activityDraftId,
        principal.getName());
    var uploaded =
        activityService.uploadDraftBanner(
            activityDraftId,
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize(),
            readBytes(file));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(fileDTOMapper.toFileDTO(uploaded, extractOrigin(request)));
  }

  @DeleteMapping("/draft/{activityDraftId}/banner")
  public ResponseEntity<Void> deleteDraftBanner(
      Principal principal, @PathVariable UUID activityDraftId) {
    log.debug(
        "Received request to delete banner of activity draft [{}] by user [{}]",
        activityDraftId,
        principal.getName());
    activityService.deleteDraftBanner(activityDraftId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping(
      value = "/draft/{activityDraftId}/files",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<FileDTO> addDraftFile(
      HttpServletRequest request,
      Principal principal,
      @PathVariable UUID activityDraftId,
      @RequestParam("file") MultipartFile file) {
    log.debug(
        "Received request to add file to activity draft [{}] by user [{}]",
        activityDraftId,
        principal.getName());
    var uploaded =
        activityService.addDraftFile(
            activityDraftId,
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize(),
            readBytes(file));
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(fileDTOMapper.toFileDTO(uploaded, extractOrigin(request)));
  }

  @DeleteMapping("/draft/{activityDraftId}/files/{fileId}")
  public ResponseEntity<Void> deleteDraftFile(
      Principal principal, @PathVariable UUID activityDraftId, @PathVariable UUID fileId) {
    log.debug(
        "Received request to delete file [{}] of activity draft [{}] by user [{}]",
        fileId,
        activityDraftId,
        principal.getName());
    activityService.deleteDraftFile(activityDraftId, fileId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping(
      value = "/{activityId}/files/{fileId}/download",
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<byte[]> downloadActivityFile(
      Principal principal, @PathVariable UUID activityId, @PathVariable UUID fileId) {
    log.debug(
        "Received request to download file [{}] of activity [{}] by user [{}]",
        fileId,
        activityId,
        principal.getName());
    var downloadedFile = activityService.downloadActivityFile(activityId, fileId);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + downloadedFile.fileName() + "\"")
        .body(downloadedFile.content());
  }
}
