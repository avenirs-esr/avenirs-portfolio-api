package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.FeedbackDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.FeedbackStaffListItemDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.UpdateFeedbackRequest;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.FeedbackDetailsDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.FeedbackStaffListItemDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.FeedbackData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.FeedbackService;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class FeedbackControllerTest {

  @Mock private FeedbackService feedbackService;
  @Mock private FeedbackDetailsDTOMapper feedbackDetailsDTOMapper;
  @Mock private FeedbackStaffListItemDTOMapper feedbackStaffListItemDTOMapper;

  @InjectMocks private FeedbackController controller;

  private Principal principal;

  @BeforeEach
  void setUp() {
    principal = () -> "0a8700ab-90b6-4a38-8338-acbdd4fbcd3d";
  }

  @Test
  void askForFeedback_should_return_201_with_feedback_details_dto() {
    BddLogger.given("A logged-in student and a valid declared activity ID");

    UUID declaredActivityId = UUID.randomUUID();
    Feedback feedback = mock(Feedback.class);
    FeedbackDetailsDTO expectedDto =
        new FeedbackDetailsDTO(
            UUID.randomUUID(),
            declaredActivityId,
            null,
            "Ma réflexion",
            null,
            EFeedbackStatus.NEW,
            List.of(),
            List.of(),
            Instant.now(),
            Instant.now());

    when(feedbackService.createFeedback(declaredActivityId)).thenReturn(feedback);
    when(feedbackDetailsDTOMapper.toDTO(feedback)).thenReturn(expectedDto);

    BddLogger.when("askForFeedback is called");
    ResponseEntity<FeedbackDetailsDTO> response =
        controller.askForFeedback(principal, declaredActivityId);

    BddLogger.then("201 CREATED is returned with the mapped FeedbackDetailsDTO");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isEqualTo(expectedDto);
    assertThat(response.getBody().status()).isEqualTo(EFeedbackStatus.NEW);
    assertThat(response.getBody().declaredActivityId()).isEqualTo(declaredActivityId);

    verify(feedbackService).createFeedback(declaredActivityId);
    verify(feedbackDetailsDTOMapper).toDTO(feedback);
  }

  @Test
  void askForFeedback_should_delegate_to_service_with_correct_declared_activity_id() {
    BddLogger.given("A declared activity ID");

    UUID declaredActivityId = UUID.randomUUID();
    Feedback feedback = mock(Feedback.class);

    when(feedbackService.createFeedback(declaredActivityId)).thenReturn(feedback);
    when(feedbackDetailsDTOMapper.toDTO(feedback)).thenReturn(mock(FeedbackDetailsDTO.class));

    BddLogger.when("askForFeedback is called");
    controller.askForFeedback(principal, declaredActivityId);

    BddLogger.then("The service is called exactly once with the right ID");
    verify(feedbackService, times(1)).createFeedback(declaredActivityId);
    verifyNoMoreInteractions(feedbackService);
  }

  @Test
  void getFeedbackDetails_should_return_200_with_mapped_dto() {
    BddLogger.given("A feedback ID, a user category STUDENT and a service returning FeedbackData");

    UUID feedbackId = UUID.randomUUID();
    EUserCategory userCategory = EUserCategory.STUDENT;
    FeedbackData feedbackData = mock(FeedbackData.class);
    FeedbackDetailsDTO expectedDto =
        new FeedbackDetailsDTO(
            feedbackId,
            UUID.randomUUID(),
            null,
            null,
            null,
            EFeedbackStatus.NEW,
            List.of(),
            List.of(),
            Instant.now(),
            Instant.now());

    when(feedbackService.getFeedbackDetails(feedbackId, userCategory)).thenReturn(feedbackData);
    when(feedbackDetailsDTOMapper.toDTO(feedbackData)).thenReturn(expectedDto);

    BddLogger.when("getFeedbackDetails is called with STUDENT category");
    ResponseEntity<FeedbackDetailsDTO> response =
        controller.getFeedbackDetails(principal, feedbackId, userCategory);

    BddLogger.then("200 OK is returned with the mapped FeedbackDetailsDTO");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isSameAs(expectedDto);

    verify(feedbackService).getFeedbackDetails(feedbackId, userCategory);
    verify(feedbackDetailsDTOMapper).toDTO(feedbackData);
  }

  @Test
  void getFeedbackDetails_should_delegate_to_service_with_correct_id_and_category() {
    BddLogger.given("A feedback ID and a STAFF user category");

    UUID feedbackId = UUID.randomUUID();
    EUserCategory userCategory = EUserCategory.STAFF;
    FeedbackData feedbackData = mock(FeedbackData.class);

    when(feedbackService.getFeedbackDetails(feedbackId, userCategory)).thenReturn(feedbackData);
    when(feedbackDetailsDTOMapper.toDTO(feedbackData)).thenReturn(mock(FeedbackDetailsDTO.class));

    BddLogger.when("getFeedbackDetails is called with STAFF category");
    controller.getFeedbackDetails(principal, feedbackId, userCategory);

    BddLogger.then("The service is called exactly once with the correct feedback ID and category");
    verify(feedbackService, times(1)).getFeedbackDetails(feedbackId, userCategory);
    verifyNoMoreInteractions(feedbackService);
  }

  @Test
  void getStaffFeedbacks_should_return_200_with_paged_result_when_no_filters() {
    BddLogger.given("A logged-in staff with two feedbacks and no filter applied");

    Feedback feedback1 = mock(Feedback.class);
    Feedback feedback2 = mock(Feedback.class);
    FeedbackStaffListItemDTO dto1 = mock(FeedbackStaffListItemDTO.class);
    FeedbackStaffListItemDTO dto2 = mock(FeedbackStaffListItemDTO.class);

    PagedResult<Feedback> pagedResult =
        new PagedResult<>(List.of(feedback1, feedback2), new PageInfo(0, 8, 2));

    when(feedbackService.getStaffFeedbacks(isNull(), isNull(), any())).thenReturn(pagedResult);
    when(feedbackStaffListItemDTOMapper.toDTO(feedback1)).thenReturn(dto1);
    when(feedbackStaffListItemDTOMapper.toDTO(feedback2)).thenReturn(dto2);

    BddLogger.when("getStaffFeedbacks is called without any filter");
    ResponseEntity<PagedResponse<FeedbackStaffListItemDTO>> response =
        controller.getStaffFeedbacks(principal, null, null, null, null);

    BddLogger.then("200 OK is returned with 2 items and pagination info");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().data()).containsExactly(dto1, dto2);
    assertThat(response.getBody().page().totalElements()).isEqualTo(2);

    verify(feedbackService).getStaffFeedbacks(isNull(), isNull(), any());
    verify(feedbackStaffListItemDTOMapper).toDTO(feedback1);
    verify(feedbackStaffListItemDTOMapper).toDTO(feedback2);
  }

  @Test
  void getStaffFeedbacks_should_forward_status_filter_to_service() {
    BddLogger.given("A logged-in staff requesting only IN_PROCESS feedbacks");

    Feedback feedback = mock(Feedback.class);
    FeedbackStaffListItemDTO dto = mock(FeedbackStaffListItemDTO.class);

    PagedResult<Feedback> pagedResult = new PagedResult<>(List.of(feedback), new PageInfo(0, 8, 1));

    when(feedbackService.getStaffFeedbacks(eq(EFeedbackStatus.IN_PROCESS), isNull(), any()))
        .thenReturn(pagedResult);
    when(feedbackStaffListItemDTOMapper.toDTO(feedback)).thenReturn(dto);

    BddLogger.when("getStaffFeedbacks is called with status=IN_PROCESS");
    ResponseEntity<PagedResponse<FeedbackStaffListItemDTO>> response =
        controller.getStaffFeedbacks(principal, EFeedbackStatus.IN_PROCESS, null, 0, 8);

    BddLogger.then("service is called with IN_PROCESS and result has 1 item");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().data()).hasSize(1);

    verify(feedbackService).getStaffFeedbacks(eq(EFeedbackStatus.IN_PROCESS), isNull(), any());
  }

  @Test
  void getStaffFeedbacks_should_forward_activity_id_filter_to_service() {
    BddLogger.given("A logged-in staff requesting feedbacks for a specific activity");

    UUID activityId = UUID.randomUUID();
    Feedback feedback = mock(Feedback.class);
    FeedbackStaffListItemDTO dto = mock(FeedbackStaffListItemDTO.class);

    PagedResult<Feedback> pagedResult = new PagedResult<>(List.of(feedback), new PageInfo(0, 8, 1));

    when(feedbackService.getStaffFeedbacks(isNull(), eq(activityId), any()))
        .thenReturn(pagedResult);
    when(feedbackStaffListItemDTOMapper.toDTO(feedback)).thenReturn(dto);

    BddLogger.when("getStaffFeedbacks is called with activityId");
    ResponseEntity<PagedResponse<FeedbackStaffListItemDTO>> response =
        controller.getStaffFeedbacks(principal, null, activityId, null, null);

    BddLogger.then("service is called with the activityId and result has 1 item");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().data()).hasSize(1);

    verify(feedbackService).getStaffFeedbacks(isNull(), eq(activityId), any());
  }

  @Test
  void getStaffFeedbacks_should_return_empty_list_when_staff_has_no_feedbacks() {
    BddLogger.given("A logged-in staff with no feedbacks");

    PagedResult<Feedback> emptyResult = new PagedResult<>(List.of(), new PageInfo(0, 8, 0));

    when(feedbackService.getStaffFeedbacks(isNull(), isNull(), any())).thenReturn(emptyResult);

    BddLogger.when("getStaffFeedbacks is called");
    ResponseEntity<PagedResponse<FeedbackStaffListItemDTO>> response =
        controller.getStaffFeedbacks(principal, null, null, 0, 8);

    BddLogger.then("200 OK is returned with empty data and totalElements=0");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().data()).isEmpty();
    assertThat(response.getBody().page().totalElements()).isZero();

    verifyNoInteractions(feedbackStaffListItemDTOMapper);
  }

  @Test
  void updateFeedback_should_return_204_no_content() {
    BddLogger.given("A valid feedback ID and a staff writing their feedback text");

    UUID feedbackId = UUID.randomUUID();
    UpdateFeedbackRequest request = new UpdateFeedbackRequest("Excellent travail, bravo !");

    doNothing().when(feedbackService).updateFeedback(feedbackId, request.feedback());

    BddLogger.when("updateFeedback is called");
    ResponseEntity<Void> response = controller.updateFeedback(principal, feedbackId, request);

    BddLogger.then("204 No Content is returned with no body");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response.getBody()).isNull();

    verify(feedbackService).updateFeedback(feedbackId, request.feedback());
  }

  @Test
  void updateFeedback_should_delegate_to_service_with_correct_feedback_id_and_text() {
    BddLogger.given("A feedback ID and a request with a specific feedback text");

    UUID feedbackId = UUID.randomUUID();
    String feedbackText = "Très bon travail sur cette activité.";
    UpdateFeedbackRequest request = new UpdateFeedbackRequest(feedbackText);

    BddLogger.when("updateFeedback is called");
    controller.updateFeedback(principal, feedbackId, request);

    BddLogger.then("The service is called exactly once with the correct feedback ID and text");
    verify(feedbackService, times(1)).updateFeedback(feedbackId, feedbackText);
    verifyNoMoreInteractions(feedbackService);
  }
}
