package fr.avenirsesr.portfolio.student.activity.application.adapter.controller;

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
import fr.avenirsesr.portfolio.file.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.file.application.adapter.mapper.FileDtoMapper;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.model.FileDownload;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityContentDTO;
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
import fr.avenirsesr.portfolio.student.activity.domain.data.FeedbackDashboardData;
import fr.avenirsesr.portfolio.student.activity.domain.data.FeedbackData;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.FeedbackService;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class FeedbackControllerTest {

  @Mock private FeedbackService feedbackService;
  @Mock private FeedbackDetailsDTOMapper feedbackDetailsDTOMapper;
  @Mock private FeedbackStaffListItemDTOMapper feedbackStaffListItemDTOMapper;
  @Mock private StudentFeedbackItemListDTOMapper studentFeedbackItemListDTOMapper;
  @Mock private FeedbackOverviewDTOMapper feedbackOverviewDTOMapper;
  @Mock private FileDtoMapper fileDtoMapper;

  @InjectMocks private FeedbackController controller;

  private Principal principal;

  @BeforeEach
  void setUp() {
    principal = () -> "0a8700ab-90b6-4a38-8338-acbdd4fbcd3d";
  }

  @Nested
  class AskForFeedback {

    @Test
    void should_return_201_with_feedback_details_dto() {
      BddLogger.given("A logged-in student and a valid declared activity ID");

      UUID declaredActivityId = UUID.randomUUID();
      Feedback feedback = mock(Feedback.class);
      FeedbackDetailsDTO expectedDto =
          new FeedbackDetailsDTO(
              UUID.randomUUID(),
              UUID.randomUUID(),
              mock(ActivityContentDTO.class),
              null,
              "Ma réflexion",
              null,
              EFeedbackStatus.NEW,
              List.of(),
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
      assertThat(response.getBody().activity()).isNotNull();

      verify(feedbackService).createFeedback(declaredActivityId);
      verify(feedbackDetailsDTOMapper).toDTO(feedback);
    }

    @Test
    void should_delegate_to_service_with_correct_declared_activity_id() {
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
  }

  @Nested
  class GetFeedbackDetails {

    @Test
    void should_return_200_with_mapped_dto() {
      BddLogger.given(
          "A feedback ID, a user category STUDENT and a service returning FeedbackData");

      UUID feedbackId = UUID.randomUUID();
      EUserCategory userCategory = EUserCategory.STUDENT;
      FeedbackData feedbackData = mock(FeedbackData.class);
      FeedbackDetailsDTO expectedDto =
          new FeedbackDetailsDTO(
              feedbackId,
              UUID.randomUUID(),
              mock(ActivityContentDTO.class),
              null,
              null,
              null,
              EFeedbackStatus.NEW,
              List.of(),
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
    void should_delegate_to_service_with_correct_id_and_category() {
      BddLogger.given("A feedback ID and a STAFF user category");

      UUID feedbackId = UUID.randomUUID();
      EUserCategory userCategory = EUserCategory.STAFF;
      FeedbackData feedbackData = mock(FeedbackData.class);

      when(feedbackService.getFeedbackDetails(feedbackId, userCategory)).thenReturn(feedbackData);
      when(feedbackDetailsDTOMapper.toDTO(feedbackData)).thenReturn(mock(FeedbackDetailsDTO.class));

      BddLogger.when("getFeedbackDetails is called with STAFF category");
      controller.getFeedbackDetails(principal, feedbackId, userCategory);

      BddLogger.then(
          "The service is called exactly once with the correct feedback ID and category");
      verify(feedbackService, times(1)).getFeedbackDetails(feedbackId, userCategory);
      verifyNoMoreInteractions(feedbackService);
    }
  }

  @Nested
  class GetStaffFeedbacks {

    private Feedback feedbackWithLatestId(UUID declaredActivityId, UUID latestFeedbackId) {
      Feedback feedback = mock(Feedback.class);
      DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
      when(feedback.getDeclaredActivity()).thenReturn(declaredActivity);
      when(declaredActivity.getId()).thenReturn(declaredActivityId);

      Feedback latestFeedback = mock(Feedback.class);
      when(latestFeedback.getId()).thenReturn(latestFeedbackId);
      when(feedbackService.getLatestFeedback(declaredActivityId)).thenReturn(latestFeedback);

      return feedback;
    }

    @Test
    void should_return_200_with_paged_result_when_no_filters() {
      BddLogger.given("A logged-in staff with two feedbacks and no filter applied");

      UUID latestFeedbackId1 = UUID.randomUUID();
      UUID latestFeedbackId2 = UUID.randomUUID();
      Feedback feedback1 = feedbackWithLatestId(UUID.randomUUID(), latestFeedbackId1);
      Feedback feedback2 = feedbackWithLatestId(UUID.randomUUID(), latestFeedbackId2);
      FeedbackStaffListItemDTO dto1 = mock(FeedbackStaffListItemDTO.class);
      FeedbackStaffListItemDTO dto2 = mock(FeedbackStaffListItemDTO.class);

      PagedResult<Feedback> pagedResult =
          new PagedResult<>(List.of(feedback1, feedback2), new PageInfo(0, 8, 2));

      when(feedbackService.getStaffFeedbacks(isNull(), isNull(), any())).thenReturn(pagedResult);
      when(feedbackStaffListItemDTOMapper.toDTO(feedback1, latestFeedbackId1)).thenReturn(dto1);
      when(feedbackStaffListItemDTOMapper.toDTO(feedback2, latestFeedbackId2)).thenReturn(dto2);

      BddLogger.when("getStaffFeedbacks is called without any filter");
      ResponseEntity<PagedResponse<FeedbackStaffListItemDTO>> response =
          controller.getStaffFeedbacks(principal, null, null, null, null);

      BddLogger.then("200 OK is returned with 2 items and pagination info");
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().data()).containsExactly(dto1, dto2);
      assertThat(response.getBody().page().totalElements()).isEqualTo(2);

      verify(feedbackService).getStaffFeedbacks(isNull(), isNull(), any());
      verify(feedbackStaffListItemDTOMapper).toDTO(feedback1, latestFeedbackId1);
      verify(feedbackStaffListItemDTOMapper).toDTO(feedback2, latestFeedbackId2);
    }

    @Test
    void should_forward_statuses_filter_to_service() {
      BddLogger.given("A logged-in staff requesting only IN_PROCESS feedbacks");

      UUID latestFeedbackId = UUID.randomUUID();
      Feedback feedback = feedbackWithLatestId(UUID.randomUUID(), latestFeedbackId);
      FeedbackStaffListItemDTO dto = mock(FeedbackStaffListItemDTO.class);

      PagedResult<Feedback> pagedResult =
          new PagedResult<>(List.of(feedback), new PageInfo(0, 8, 1));

      List<EFeedbackStatus> statuses = List.of(EFeedbackStatus.IN_PROCESS);

      when(feedbackService.getStaffFeedbacks(eq(statuses), isNull(), any()))
          .thenReturn(pagedResult);
      when(feedbackStaffListItemDTOMapper.toDTO(feedback, latestFeedbackId)).thenReturn(dto);

      BddLogger.when("getStaffFeedbacks is called with status=IN_PROCESS");
      ResponseEntity<PagedResponse<FeedbackStaffListItemDTO>> response =
          controller.getStaffFeedbacks(principal, statuses, null, 0, 8);

      BddLogger.then("service is called with IN_PROCESS and result has 1 item");
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody().data()).hasSize(1);

      verify(feedbackService).getStaffFeedbacks(eq(statuses), isNull(), any());
    }

    @Test
    void should_forward_multiple_statuses_filter_to_service() {
      BddLogger.given("A logged-in staff requesting feedbacks with multiple statuses");

      UUID latestFeedbackId1 = UUID.randomUUID();
      UUID latestFeedbackId2 = UUID.randomUUID();
      Feedback feedback1 = feedbackWithLatestId(UUID.randomUUID(), latestFeedbackId1);
      Feedback feedback2 = feedbackWithLatestId(UUID.randomUUID(), latestFeedbackId2);
      FeedbackStaffListItemDTO dto1 = mock(FeedbackStaffListItemDTO.class);
      FeedbackStaffListItemDTO dto2 = mock(FeedbackStaffListItemDTO.class);

      PagedResult<Feedback> pagedResult =
          new PagedResult<>(List.of(feedback1, feedback2), new PageInfo(0, 8, 2));

      List<EFeedbackStatus> statuses = List.of(EFeedbackStatus.SUBMITTED, EFeedbackStatus.SEEN);

      when(feedbackService.getStaffFeedbacks(eq(statuses), isNull(), any()))
          .thenReturn(pagedResult);
      when(feedbackStaffListItemDTOMapper.toDTO(feedback1, latestFeedbackId1)).thenReturn(dto1);
      when(feedbackStaffListItemDTOMapper.toDTO(feedback2, latestFeedbackId2)).thenReturn(dto2);

      BddLogger.when("getStaffFeedbacks is called with multiple statuses");
      ResponseEntity<PagedResponse<FeedbackStaffListItemDTO>> response =
          controller.getStaffFeedbacks(principal, statuses, null, 0, 8);

      BddLogger.then("service is called with both statuses and result contains 2 items");
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().data()).containsExactly(dto1, dto2);
      assertThat(response.getBody().page().totalElements()).isEqualTo(2);

      verify(feedbackService).getStaffFeedbacks(eq(statuses), isNull(), any());
    }

    @Test
    void should_forward_activity_id_filter_to_service() {
      BddLogger.given("A logged-in staff requesting feedbacks for a specific activity");

      UUID activityId = UUID.randomUUID();
      UUID latestFeedbackId = UUID.randomUUID();
      Feedback feedback = feedbackWithLatestId(UUID.randomUUID(), latestFeedbackId);
      FeedbackStaffListItemDTO dto = mock(FeedbackStaffListItemDTO.class);

      PagedResult<Feedback> pagedResult =
          new PagedResult<>(List.of(feedback), new PageInfo(0, 8, 1));

      when(feedbackService.getStaffFeedbacks(isNull(), eq(activityId), any()))
          .thenReturn(pagedResult);
      when(feedbackStaffListItemDTOMapper.toDTO(feedback, latestFeedbackId)).thenReturn(dto);

      BddLogger.when("getStaffFeedbacks is called with activityId");
      ResponseEntity<PagedResponse<FeedbackStaffListItemDTO>> response =
          controller.getStaffFeedbacks(principal, null, activityId, null, null);

      BddLogger.then("service is called with the activityId and result has 1 item");
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody().data()).hasSize(1);

      verify(feedbackService).getStaffFeedbacks(isNull(), eq(activityId), any());
    }

    @Test
    void should_return_empty_list_when_staff_has_no_feedbacks() {
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
  }

  @Nested
  class UpdateFeedback {

    @Test
    void should_return_204_no_content() {
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
    void should_delegate_to_service_with_correct_feedback_id_and_text() {
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

  @Nested
  class SubmitFeedback {

    @Test
    void should_return_204_no_content() {
      BddLogger.given("A valid feedback ID and a staff ready to submit");

      UUID feedbackId = UUID.randomUUID();
      doNothing().when(feedbackService).submitFeedback(feedbackId);

      BddLogger.when("submitFeedback is called");
      ResponseEntity<Void> response = controller.submitFeedback(principal, feedbackId);

      BddLogger.then("204 No Content is returned with no body");
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      assertThat(response.getBody()).isNull();

      verify(feedbackService).submitFeedback(feedbackId);
    }

    @Test
    void should_delegate_to_service_with_correct_feedback_id() {
      BddLogger.given("A feedback ID");

      UUID feedbackId = UUID.randomUUID();

      BddLogger.when("submitFeedback is called");
      controller.submitFeedback(principal, feedbackId);

      BddLogger.then("The service is called exactly once with the correct feedback ID");
      verify(feedbackService, times(1)).submitFeedback(feedbackId);
      verifyNoMoreInteractions(feedbackService);
    }
  }

  @Nested
  class GetFeedbacksByActivity {

    @Test
    void should_return_200_with_mapped_list() {
      BddLogger.given("A logged-in staff and an activity with two feedbacks");

      UUID activityId = UUID.randomUUID();
      Feedback feedback1 = mock(Feedback.class);
      Feedback feedback2 = mock(Feedback.class);
      StudentFeedbackItemListDTO dto1 = mock(StudentFeedbackItemListDTO.class);
      StudentFeedbackItemListDTO dto2 = mock(StudentFeedbackItemListDTO.class);

      when(feedbackService.getFeedbacksByActivity(activityId))
          .thenReturn(List.of(feedback1, feedback2));
      when(studentFeedbackItemListDTOMapper.toDTO(feedback1)).thenReturn(dto1);
      when(studentFeedbackItemListDTOMapper.toDTO(feedback2)).thenReturn(dto2);

      BddLogger.when("getFeedbacksByActivity is called");
      ResponseEntity<List<StudentFeedbackItemListDTO>> response =
          controller.getFeedbacksByActivity(principal, activityId);

      BddLogger.then("200 OK is returned with 2 mapped items");
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).containsExactly(dto1, dto2);

      verify(feedbackService).getFeedbacksByActivity(activityId);
      verify(studentFeedbackItemListDTOMapper).toDTO(feedback1);
      verify(studentFeedbackItemListDTOMapper).toDTO(feedback2);
    }

    @Test
    void should_forward_activityId_to_service() {
      BddLogger.given("A specific activity ID");

      UUID activityId = UUID.randomUUID();
      Feedback feedback = mock(Feedback.class);

      when(feedbackService.getFeedbacksByActivity(activityId)).thenReturn(List.of(feedback));
      when(studentFeedbackItemListDTOMapper.toDTO(feedback))
          .thenReturn(mock(StudentFeedbackItemListDTO.class));

      BddLogger.when("getFeedbacksByActivity is called");
      controller.getFeedbacksByActivity(principal, activityId);

      BddLogger.then("The service is called exactly once with the correct activityId");
      verify(feedbackService, times(1)).getFeedbacksByActivity(activityId);
      verifyNoMoreInteractions(feedbackService);
    }

    @Test
    void should_return_empty_list_when_activity_has_no_feedbacks() {
      BddLogger.given("An activity ID for which no feedbacks exist");

      UUID activityId = UUID.randomUUID();

      when(feedbackService.getFeedbacksByActivity(activityId)).thenReturn(List.of());

      BddLogger.when("getFeedbacksByActivity is called");
      ResponseEntity<List<StudentFeedbackItemListDTO>> response =
          controller.getFeedbacksByActivity(principal, activityId);

      BddLogger.then("200 OK is returned with an empty list");
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEmpty();

      verifyNoInteractions(studentFeedbackItemListDTOMapper);
    }
  }

  @Nested
  class GetFeedbackDashboard {

    @Test
    void should_return_200_with_correct_counts() {
      BddLogger.given(
          "A logged-in staff and the service returning a dashboard with counts 2/5/3/8");

      FeedbackDashboardData dashboard = new FeedbackDashboardData(2, 5, 3, 8);
      when(feedbackService.getFeedbackDashboard(null)).thenReturn(dashboard);

      BddLogger.when("getFeedbackDashboard is called without activityId");
      ResponseEntity<FeedbackDashboardDTO> response =
          controller.getFeedbackDashboard(principal, null);

      BddLogger.then(
          "200 OK is returned with newFeedbacks=2, pendingFeedbacks=5, processedFeedbacks=3,"
              + " totalFeedbacks=8");
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().newFeedbacks()).isEqualTo(2);
      assertThat(response.getBody().pendingFeedbacks()).isEqualTo(5);
      assertThat(response.getBody().processedFeedbacks()).isEqualTo(3);
      assertThat(response.getBody().totalFeedbacks()).isEqualTo(8);

      verify(feedbackService).getFeedbackDashboard(null);
    }

    @Test
    void should_forward_activityId_to_service() {
      BddLogger.given("A logged-in staff and a specific activityId");

      UUID activityId = UUID.randomUUID();
      when(feedbackService.getFeedbackDashboard(activityId))
          .thenReturn(new FeedbackDashboardData(1, 1, 0, 1));

      BddLogger.when("getFeedbackDashboard is called with activityId");
      controller.getFeedbackDashboard(principal, activityId);

      BddLogger.then("The service is called exactly once with the provided activityId");
      verify(feedbackService, times(1)).getFeedbackDashboard(activityId);
      verifyNoMoreInteractions(feedbackService);
    }

    @Test
    void should_return_all_zeros_when_staff_has_no_feedbacks() {
      BddLogger.given("A logged-in staff with no feedbacks");

      when(feedbackService.getFeedbackDashboard(null))
          .thenReturn(new FeedbackDashboardData(0, 0, 0, 0));

      BddLogger.when("getFeedbackDashboard is called");
      ResponseEntity<FeedbackDashboardDTO> response =
          controller.getFeedbackDashboard(principal, null);

      BddLogger.then("200 OK is returned with all counts at zero");
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody().newFeedbacks()).isZero();
      assertThat(response.getBody().pendingFeedbacks()).isZero();
      assertThat(response.getBody().processedFeedbacks()).isZero();
      assertThat(response.getBody().totalFeedbacks()).isZero();
    }

    @Test
    void should_forward_null_activityId_when_not_provided() {
      BddLogger.given("A logged-in staff calling the dashboard endpoint without activityId");

      when(feedbackService.getFeedbackDashboard(null))
          .thenReturn(new FeedbackDashboardData(0, 0, 0, 0));

      BddLogger.when("getFeedbackDashboard is called with activityId=null");
      controller.getFeedbackDashboard(principal, null);

      BddLogger.then("The service is called with null activityId");
      verify(feedbackService).getFeedbackDashboard(null);
    }
  }

  @Nested
  class GetFeedbackHistory {

    @Test
    void should_return_200_with_mapped_list() {
      BddLogger.given("A logged-in staff and a declared activity with two feedbacks");

      UUID declaredActivityId = UUID.randomUUID();
      Feedback feedback1 = mock(Feedback.class);
      Feedback feedback2 = mock(Feedback.class);
      FeedbackOverviewDTO dto1 = mock(FeedbackOverviewDTO.class);
      FeedbackOverviewDTO dto2 = mock(FeedbackOverviewDTO.class);

      when(feedbackService.getFeedbackHistory(declaredActivityId))
          .thenReturn(List.of(feedback1, feedback2));
      when(feedbackOverviewDTOMapper.toDTO(feedback1)).thenReturn(dto1);
      when(feedbackOverviewDTOMapper.toDTO(feedback2)).thenReturn(dto2);

      BddLogger.when("getFeedbackHistory is called");
      ResponseEntity<List<FeedbackOverviewDTO>> response =
          controller.getFeedbackHistory(principal, declaredActivityId);

      BddLogger.then("200 OK is returned with 2 mapped FeedbackOverviewDTOs");
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).containsExactly(dto1, dto2);

      verify(feedbackService).getFeedbackHistory(declaredActivityId);
      verify(feedbackOverviewDTOMapper).toDTO(feedback1);
      verify(feedbackOverviewDTOMapper).toDTO(feedback2);
    }

    @Test
    void should_return_empty_list_when_no_feedbacks_exist() {
      BddLogger.given("A logged-in staff and a declared activity with no feedbacks");

      UUID declaredActivityId = UUID.randomUUID();

      when(feedbackService.getFeedbackHistory(declaredActivityId)).thenReturn(List.of());

      BddLogger.when("getFeedbackHistory is called");
      ResponseEntity<List<FeedbackOverviewDTO>> response =
          controller.getFeedbackHistory(principal, declaredActivityId);

      BddLogger.then("200 OK is returned with an empty list");
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEmpty();

      verifyNoInteractions(feedbackOverviewDTOMapper);
    }

    @Test
    void should_delegate_to_service_with_correct_declared_activity_id() {
      BddLogger.given("A declared activity ID");

      UUID declaredActivityId = UUID.randomUUID();
      Feedback feedback = mock(Feedback.class);

      when(feedbackService.getFeedbackHistory(declaredActivityId)).thenReturn(List.of(feedback));
      when(feedbackOverviewDTOMapper.toDTO(feedback)).thenReturn(mock(FeedbackOverviewDTO.class));

      BddLogger.when("getFeedbackHistory is called");
      controller.getFeedbackHistory(principal, declaredActivityId);

      BddLogger.then("The service is called exactly once with the correct declaredActivityId");
      verify(feedbackService, times(1)).getFeedbackHistory(declaredActivityId);
      verifyNoMoreInteractions(feedbackService);
    }
  }

  @Nested
  class UploadAttachment {

    private MockMultipartFile multipartFile() {
      return new MockMultipartFile(
          "file", "retour.pdf", "application/pdf", "content".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void should_return_201_with_the_mapped_file_dto() {
      BddLogger.given("A feedback ID and a file uploaded by the staff author");

      UUID feedbackId = UUID.randomUUID();
      MockMultipartFile file = multipartFile();
      File uploaded = mock(File.class);
      FileDTO expectedDto =
          new FileDTO(
              UUID.randomUUID(), "retour.pdf", EFileType.PDF, file.getSize(), "url", Instant.now());

      when(feedbackService.uploadAttachment(
              eq(feedbackId),
              eq("retour.pdf"),
              eq("application/pdf"),
              eq(file.getSize()),
              any(byte[].class)))
          .thenReturn(uploaded);
      when(fileDtoMapper.fromDomain(uploaded)).thenReturn(expectedDto);

      BddLogger.when("uploadAttachment is called");
      ResponseEntity<FileDTO> response =
          controller.uploadFeedbackAttachment(principal, feedbackId, file);

      BddLogger.then("201 CREATED is returned with the mapped FileDTO");
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      assertThat(response.getBody()).isEqualTo(expectedDto);

      verify(fileDtoMapper).fromDomain(uploaded);
    }

    @Test
    void should_delegate_to_service_with_the_file_metadata() {
      BddLogger.given("A feedback ID and an uploaded file");

      UUID feedbackId = UUID.randomUUID();
      MockMultipartFile file = multipartFile();

      BddLogger.when("uploadAttachment is called");
      controller.uploadFeedbackAttachment(principal, feedbackId, file);

      BddLogger.then("The service is called once with the file name, mime type, size and content");
      verify(feedbackService, times(1))
          .uploadAttachment(
              feedbackId,
              "retour.pdf",
              "application/pdf",
              file.getSize(),
              "content".getBytes(StandardCharsets.UTF_8));
      verifyNoMoreInteractions(feedbackService);
    }
  }

  @Nested
  class DeleteAttachment {

    @Test
    void should_return_204_no_content() {
      BddLogger.given("A feedback ID and one of its attachment IDs");

      UUID feedbackId = UUID.randomUUID();
      UUID attachmentId = UUID.randomUUID();
      doNothing().when(feedbackService).deleteAttachment(feedbackId, attachmentId);

      BddLogger.when("deleteAttachment is called");
      ResponseEntity<Void> response =
          controller.deleteFeedbackAttachment(principal, feedbackId, attachmentId);

      BddLogger.then("204 No Content is returned with no body");
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      assertThat(response.getBody()).isNull();

      verify(feedbackService).deleteAttachment(feedbackId, attachmentId);
    }

    @Test
    void should_delegate_to_service_with_correct_feedback_and_attachment_ids() {
      BddLogger.given("A feedback ID and an attachment ID");

      UUID feedbackId = UUID.randomUUID();
      UUID attachmentId = UUID.randomUUID();

      BddLogger.when("deleteAttachment is called");
      controller.deleteFeedbackAttachment(principal, feedbackId, attachmentId);

      BddLogger.then("The service is called exactly once with both IDs");
      verify(feedbackService, times(1)).deleteAttachment(feedbackId, attachmentId);
      verifyNoMoreInteractions(feedbackService);
    }
  }

  @Nested
  class DownloadAttachment {

    @Test
    void should_return_200_with_the_file_content_and_content_disposition_header() {
      BddLogger.given("A feedback ID and one of its attachment IDs");

      UUID feedbackId = UUID.randomUUID();
      UUID attachmentId = UUID.randomUUID();
      byte[] content = "content".getBytes(StandardCharsets.UTF_8);

      when(feedbackService.downloadAttachment(feedbackId, attachmentId))
          .thenReturn(new FileDownload("retour.pdf", content));

      BddLogger.when("downloadAttachment is called");
      ResponseEntity<byte[]> response =
          controller.downloadFeedbackAttachment(principal, feedbackId, attachmentId);

      BddLogger.then("200 OK is returned with the file content as an octet stream");
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo(content);
      assertThat(response.getHeaders().getContentType())
          .isEqualTo(MediaType.APPLICATION_OCTET_STREAM);
      assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
          .isEqualTo("attachment; filename=\"retour.pdf\"");

      verify(feedbackService).downloadAttachment(feedbackId, attachmentId);
    }

    @Test
    void should_delegate_to_service_with_correct_feedback_and_attachment_ids() {
      BddLogger.given("A feedback ID and an attachment ID");

      UUID feedbackId = UUID.randomUUID();
      UUID attachmentId = UUID.randomUUID();

      when(feedbackService.downloadAttachment(feedbackId, attachmentId))
          .thenReturn(new FileDownload("retour.pdf", new byte[0]));

      BddLogger.when("downloadAttachment is called");
      controller.downloadFeedbackAttachment(principal, feedbackId, attachmentId);

      BddLogger.then("The service is called exactly once with both IDs");
      verify(feedbackService, times(1)).downloadAttachment(feedbackId, attachmentId);
      verifyNoMoreInteractions(feedbackService);
    }
  }
}
