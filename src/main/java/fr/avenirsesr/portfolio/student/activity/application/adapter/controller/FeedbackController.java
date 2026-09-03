package fr.avenirsesr.portfolio.student.activity.application.adapter.controller;

import static fr.avenirsesr.portfolio.shared.application.adapter.Utils.readBytes;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.file.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.file.application.adapter.mapper.FileDtoMapper;
import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.FeedbackDashboardDTO;
import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.FeedbackDetailsDTO;
import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.FeedbackOverviewDTO;
import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.FeedbackStaffListItemDTO;
import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.StudentFeedbackItemListDTO;
import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.UpdateFeedbackRequest;
import fr.avenirsesr.portfolio.student.activity.application.adapter.mapper.FeedbackDetailsDTOMapper;
import fr.avenirsesr.portfolio.student.activity.application.adapter.mapper.FeedbackOverviewDTOMapper;
import fr.avenirsesr.portfolio.student.activity.application.adapter.mapper.FeedbackStaffListItemDTOMapper;
import fr.avenirsesr.portfolio.student.activity.application.adapter.mapper.StudentFeedbackItemListDTOMapper;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.FeedbackService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/me/activity-progress/feedbacks")
public class FeedbackController {
  private final FeedbackService feedbackService;
  private final FeedbackDetailsDTOMapper feedbackDetailsDTOMapper;
  private final FeedbackStaffListItemDTOMapper feedbackStaffListItemDTOMapper;
  private final StudentFeedbackItemListDTOMapper studentFeedbackItemListDTOMapper;
  private final FeedbackOverviewDTOMapper feedbackOverviewDTOMapper;
  private final FileDtoMapper fileDtoMapper;

  @PreAuthorize("hasAuthority('feedback:request:read:assigned')")
  @GetMapping
  public ResponseEntity<PagedResponse<FeedbackStaffListItemDTO>> getStaffFeedbacks(
      Principal principal,
      @Parameter(schema = @Schema(ref = "#/components/schemas/EFeedbackStatus"))
          @RequestParam(required = false)
          List<EFeedbackStatus> statuses,
      @RequestParam(required = false) UUID activityId,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to get staff feedbacks for user [{}] (statuses={}, activityId={},"
            + " page={}, pageSize={})",
        principal.getName(),
        statuses,
        activityId,
        pageCriteria.page(),
        pageCriteria.pageSize());
    var result = feedbackService.getStaffFeedbacks(statuses, activityId, pageCriteria);

    var latestFeedbackIdsByDeclaredActivity = new HashMap<UUID, UUID>();
    var content =
        result.content().stream()
            .map(
                feedback -> {
                  var declaredActivityId = feedback.getDeclaredActivity().getId();
                  var latestFeedbackId =
                      latestFeedbackIdsByDeclaredActivity.computeIfAbsent(
                          declaredActivityId, id -> feedbackService.getLatestFeedback(id).getId());
                  return feedbackStaffListItemDTOMapper.toDTO(feedback, latestFeedbackId);
                })
            .toList();

    return ResponseEntity.ok(
        new PagedResponse<>(content, PageInfoDTO.fromDomain(result.pageInfo())));
  }

  @PreAuthorize("hasAuthority('feedback:history:read:contextual')")
  @GetMapping("/exhaustive-list/{activityId}")
  public ResponseEntity<List<StudentFeedbackItemListDTO>> getFeedbacksByActivity(
      Principal principal, @PathVariable UUID activityId) {
    log.debug(
        "Received request to get all feedbacks for activity [{}] by user [{}]",
        activityId,
        principal.getName());
    return ResponseEntity.ok(
        feedbackService.getFeedbacksByActivity(activityId).stream()
            .map(studentFeedbackItemListDTOMapper::toDTO)
            .toList());
  }

  @PreAuthorize("hasAnyAuthority('feedback:received:read:own','feedback:request:read:assigned')")
  @GetMapping("/{userCategory}/{feedbackId}")
  public ResponseEntity<FeedbackDetailsDTO> getFeedbackDetails(
      Principal principal,
      @Valid @PathVariable UUID feedbackId,
      @PathVariable EUserCategory userCategory) {
    log.debug(
        "Received request to get feedback details [{}] by user [{}] (userCategory={})",
        feedbackId,
        principal.getName(),
        userCategory);
    return ResponseEntity.ok(
        feedbackDetailsDTOMapper.toDTO(
            feedbackService.getFeedbackDetails(feedbackId, userCategory)));
  }

  @PreAuthorize("hasAuthority('feedback:request:respond:assigned')")
  @PutMapping("/{feedbackId}")
  public ResponseEntity<Void> updateFeedback(
      Principal principal,
      @Valid @PathVariable UUID feedbackId,
      @Valid @RequestBody UpdateFeedbackRequest request) {
    log.debug(
        "Received request to update feedback [{}] by user [{}]", feedbackId, principal.getName());
    feedbackService.updateFeedback(feedbackId, request.feedback());
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasAuthority('feedback:request:create:own')")
  @PostMapping("/{declaredActivityId}/ask-for-feedback")
  public ResponseEntity<FeedbackDetailsDTO> askForFeedback(
      Principal principal, @Valid @PathVariable UUID declaredActivityId) {
    log.debug(
        "Received request to ask for feedback on declared activity [{}] by student [{}]",
        declaredActivityId,
        principal.getName());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(feedbackDetailsDTOMapper.toDTO(feedbackService.createFeedback(declaredActivityId)));
  }

  @PreAuthorize("hasAuthority('feedback:dashboard:read:contextual')")
  @GetMapping("/dashboard")
  public ResponseEntity<FeedbackDashboardDTO> getFeedbackDashboard(
      Principal principal, @RequestParam(required = false) UUID activityId) {
    log.debug(
        "Received request to get feedback dashboard for user [{}] (activityId={})",
        principal.getName(),
        activityId);
    var dashboard = feedbackService.getFeedbackDashboard(activityId);
    return ResponseEntity.ok(
        new FeedbackDashboardDTO(
            dashboard.newFeedbacks(),
            dashboard.pendingFeedbacks(),
            dashboard.processedFeedbacks(),
            dashboard.totalFeedbacks()));
  }

  @PreAuthorize("hasAuthority('feedback:history:read:contextual')")
  @GetMapping("/{declaredActivityId}/history")
  public ResponseEntity<List<FeedbackOverviewDTO>> getFeedbackHistory(
      Principal principal, @PathVariable UUID declaredActivityId) {
    log.debug(
        "Received request to get feedback history for declared activity [{}] by user [{}]",
        declaredActivityId,
        principal.getName());
    return ResponseEntity.ok(
        feedbackService.getFeedbackHistory(declaredActivityId).stream()
            .map(feedbackOverviewDTOMapper::toDTO)
            .toList());
  }

  @PreAuthorize("hasAuthority('feedback:request:respond:assigned')")
  @PostMapping("/{feedbackId}/submit")
  public ResponseEntity<Void> submitFeedback(
      Principal principal, @Valid @PathVariable UUID feedbackId) {
    log.debug(
        "Received request to submit feedback [{}] by user [{}]", feedbackId, principal.getName());
    feedbackService.submitFeedback(feedbackId);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasAuthority('feedback:request:respond:assigned')")
  @PostMapping(value = "/{feedbackId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<FileDTO> uploadFeedbackAttachment(
      Principal principal,
      @Valid @PathVariable UUID feedbackId,
      @RequestParam("file") MultipartFile file) {
    log.debug(
        "Received request to upload an attachment on feedback [{}] by user [{}]",
        feedbackId,
        principal.getName());
    var uploaded =
        feedbackService.uploadAttachment(
            feedbackId,
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize(),
            readBytes(file));
    return ResponseEntity.status(HttpStatus.CREATED).body(fileDtoMapper.fromDomain(uploaded));
  }

  @PreAuthorize("hasAuthority('feedback:request:respond:assigned')")
  @DeleteMapping("/{feedbackId}/attachments/{attachmentId}")
  public ResponseEntity<Void> deleteFeedbackAttachment(
      Principal principal,
      @Valid @PathVariable UUID feedbackId,
      @Valid @PathVariable UUID attachmentId) {
    log.debug(
        "Received request to delete attachment [{}] of feedback [{}] by user [{}]",
        attachmentId,
        feedbackId,
        principal.getName());
    feedbackService.deleteAttachment(feedbackId, attachmentId);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasAnyAuthority('feedback:received:read:own','feedback:request:read:assigned')")
  @GetMapping(
      value = "/{feedbackId}/attachments/{attachmentId}/download",
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<byte[]> downloadFeedbackAttachment(
      Principal principal,
      @Valid @PathVariable UUID feedbackId,
      @Valid @PathVariable UUID attachmentId) {
    log.debug(
        "Received request to download attachment [{}] of feedback [{}] by user [{}]",
        attachmentId,
        feedbackId,
        principal.getName());
    var downloadedFile = feedbackService.downloadAttachment(feedbackId, attachmentId);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + downloadedFile.fileName() + "\"")
        .body(downloadedFile.content());
  }
}
