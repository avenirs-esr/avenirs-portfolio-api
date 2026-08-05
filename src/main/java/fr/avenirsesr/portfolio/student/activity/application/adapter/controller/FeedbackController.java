package fr.avenirsesr.portfolio.student.activity.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
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
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/me/activity-progress/feedbacks")
public class FeedbackController {
  private final FeedbackService feedbackService;
  private final FeedbackDetailsDTOMapper feedbackDetailsDTOMapper;
  private final FeedbackStaffListItemDTOMapper feedbackStaffListItemDTOMapper;
  private final StudentFeedbackItemListDTOMapper studentFeedbackItemListDTOMapper;
  private final FeedbackOverviewDTOMapper feedbackOverviewDTOMapper;

  @GetMapping
  public ResponseEntity<PagedResponse<FeedbackStaffListItemDTO>> getStaffFeedbacks(
      Principal principal,
      @Parameter(schema = @Schema(ref = "#/components/schemas/EFeedbackStatus"))
          @RequestParam(required = false)
          EFeedbackStatus status,
      @RequestParam(required = false) UUID activityId,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to get staff feedbacks for user [{}] (status={}, activityId={}, page={},"
            + " pageSize={})",
        principal.getName(),
        status,
        activityId,
        pageCriteria.page(),
        pageCriteria.pageSize());
    var result = feedbackService.getStaffFeedbacks(status, activityId, pageCriteria);
    return ResponseEntity.ok(
        new PagedResponse<>(
            result.content().stream().map(feedbackStaffListItemDTOMapper::toDTO).toList(),
            PageInfoDTO.fromDomain(result.pageInfo())));
  }

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

  @PostMapping("/{feedbackId}/submit")
  public ResponseEntity<Void> submitFeedback(
      Principal principal, @Valid @PathVariable UUID feedbackId) {
    log.debug(
        "Received request to submit feedback [{}] by user [{}]", feedbackId, principal.getName());
    feedbackService.submitFeedback(feedbackId);
    return ResponseEntity.noContent().build();
  }
}
