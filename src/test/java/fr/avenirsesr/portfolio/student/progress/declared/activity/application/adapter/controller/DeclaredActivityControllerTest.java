package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.association.application.adapter.mapper.AssociationSearchResultDTOMapper;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.CreationResponse;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.FeedbackDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.FeedbackStaffListItemDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.SubscribeDeclaredActivityRequest;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityAssociationsDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityDetailsDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityViewDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.FeedbackDetailsDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.FeedbackStaffListItemDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityDetailsData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
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
class DeclaredActivityControllerTest {

  @Mock private DeclaredActivityService declaredActivityService;
  @Mock private DeclaredActivityViewDTOMapper declaredActivityViewDTOMapper;
  @Mock private DeclaredActivityDetailsDTOMapper declaredActivityDetailsDTOMapper;
  @Mock private DeclaredActivityAssociationsDTOMapper declaredActivityAssociationsDTOMapper;
  @Mock private AssociationSearchResultDTOMapper associationSearchResultDTOMapper;
  @Mock private FeedbackDetailsDTOMapper feedbackDetailsDTOMapper;
  @Mock private FeedbackStaffListItemDTOMapper feedbackStaffListItemDTOMapper;

  @InjectMocks private DeclaredActivityController controller;

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

    when(declaredActivityService.createFeedback(declaredActivityId)).thenReturn(feedback);
    when(feedbackDetailsDTOMapper.toDTO(feedback)).thenReturn(expectedDto);

    BddLogger.when("askForFeedback is called");
    ResponseEntity<FeedbackDetailsDTO> response =
        controller.askForFeedback(principal, declaredActivityId);

    BddLogger.then("201 CREATED is returned with the mapped FeedbackDetailsDTO");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isEqualTo(expectedDto);
    assertThat(response.getBody().status()).isEqualTo(EFeedbackStatus.NEW);
    assertThat(response.getBody().declaredActivityId()).isEqualTo(declaredActivityId);

    verify(declaredActivityService).createFeedback(declaredActivityId);
    verify(feedbackDetailsDTOMapper).toDTO(feedback);
  }

  @Test
  void
      subscribeActivity_should_return_201_with_creation_response_containing_declared_activity_id() {
    BddLogger.given("A valid activity ID and no period");
    UUID activityId = UUID.randomUUID();
    UUID expectedId = UUID.randomUUID();
    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
    when(declaredActivity.getId()).thenReturn(expectedId);
    when(declaredActivityService.subscribe(eq(activityId), isNull(), isNull()))
        .thenReturn(declaredActivity);

    BddLogger.when("subscribeActivity is called without period");
    ResponseEntity<CreationResponse> response =
        controller.subscribeActivity(activityId, new SubscribeDeclaredActivityRequest(null));

    BddLogger.then("201 CREATED is returned with the new declared activity id");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().createdItemId()).isEqualTo(expectedId);
    verify(declaredActivityService).subscribe(activityId, null, null);
    verifyNoInteractions(declaredActivityDetailsDTOMapper);
  }

  @Test
  void finish_should_return_204_no_content() {
    BddLogger.given("A valid declared activity ID");
    UUID declaredActivityId = UUID.randomUUID();
    doNothing().when(declaredActivityService).finish(declaredActivityId);

    BddLogger.when("finish is called");
    ResponseEntity<Void> response = controller.finish(principal, declaredActivityId);

    BddLogger.then("204 No Content is returned and the mapper is never called");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response.getBody()).isNull();
    verify(declaredActivityService).finish(declaredActivityId);
    verifyNoInteractions(declaredActivityDetailsDTOMapper);
  }

  @Test
  void getDeclaredActivityDetails_should_delegate_to_service_and_mapper_with_details_data() {
    BddLogger.given("A declared activity details data with feedbacks returned by the service");
    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivityDetailsData detailsData = mock(DeclaredActivityDetailsData.class);
    DeclaredActivityDetailsDTO expectedDto = mock(DeclaredActivityDetailsDTO.class);

    when(declaredActivityService.getDeclaredActivityDetails(declaredActivityId))
        .thenReturn(detailsData);
    when(declaredActivityDetailsDTOMapper.toDTO(detailsData)).thenReturn(expectedDto);

    BddLogger.when("getDeclaredActivityDetails is called");
    ResponseEntity<DeclaredActivityDetailsDTO> response =
        controller.getDeclaredActivityDetails(principal, declaredActivityId);

    BddLogger.then("200 OK is returned with the mapped DTO");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isSameAs(expectedDto);
    verify(declaredActivityService).getDeclaredActivityDetails(declaredActivityId);
    verify(declaredActivityDetailsDTOMapper).toDTO(detailsData);
  }

  @Test
  void askForFeedback_should_delegate_to_service_with_correct_declared_activity_id() {
    BddLogger.given("A declared activity ID");
    UUID declaredActivityId = UUID.randomUUID();
    Feedback feedback = mock(Feedback.class);

    when(declaredActivityService.createFeedback(declaredActivityId)).thenReturn(feedback);
    when(feedbackDetailsDTOMapper.toDTO(feedback)).thenReturn(mock(FeedbackDetailsDTO.class));

    BddLogger.when("askForFeedback is called");
    controller.askForFeedback(principal, declaredActivityId);

    BddLogger.then("The service is called exactly once with the right ID");
    verify(declaredActivityService, times(1)).createFeedback(declaredActivityId);
    verifyNoMoreInteractions(declaredActivityService);
  }

  @Test
  void getStaffFeedbacks_should_return_200_with_paged_result_when_no_status_filter() {
    BddLogger.given("A logged-in staff with two feedbacks");
    Feedback feedback1 = mock(Feedback.class);
    Feedback feedback2 = mock(Feedback.class);
    FeedbackStaffListItemDTO dto1 = mock(FeedbackStaffListItemDTO.class);
    FeedbackStaffListItemDTO dto2 = mock(FeedbackStaffListItemDTO.class);

    PagedResult<Feedback> pagedResult =
        new PagedResult<>(List.of(feedback1, feedback2), new PageInfo(0, 8, 2));

    when(declaredActivityService.getStaffFeedbacks(isNull(), any())).thenReturn(pagedResult);
    when(feedbackStaffListItemDTOMapper.toDTO(feedback1)).thenReturn(dto1);
    when(feedbackStaffListItemDTOMapper.toDTO(feedback2)).thenReturn(dto2);

    BddLogger.when("getStaffFeedbacks is called without status filter");
    ResponseEntity<PagedResponse<FeedbackStaffListItemDTO>> response =
        controller.getStaffFeedbacks(principal, null, null, null);

    BddLogger.then("200 OK is returned with 2 items and pagination info");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().data()).hasSize(2);
    assertThat(response.getBody().data()).containsExactly(dto1, dto2);
    assertThat(response.getBody().page().totalElements()).isEqualTo(2);

    verify(declaredActivityService).getStaffFeedbacks(isNull(), any());
    verify(feedbackStaffListItemDTOMapper).toDTO(feedback1);
    verify(feedbackStaffListItemDTOMapper).toDTO(feedback2);
  }

  @Test
  void getStaffFeedbacks_should_forward_status_filter_to_service() {
    BddLogger.given("A logged-in staff requesting only IN_PROCESS feedbacks");
    Feedback feedback = mock(Feedback.class);
    FeedbackStaffListItemDTO dto = mock(FeedbackStaffListItemDTO.class);

    PagedResult<Feedback> pagedResult = new PagedResult<>(List.of(feedback), new PageInfo(0, 8, 1));

    when(declaredActivityService.getStaffFeedbacks(eq(EFeedbackStatus.IN_PROCESS), any()))
        .thenReturn(pagedResult);
    when(feedbackStaffListItemDTOMapper.toDTO(feedback)).thenReturn(dto);

    BddLogger.when("getStaffFeedbacks is called with status=IN_PROCESS");
    ResponseEntity<PagedResponse<FeedbackStaffListItemDTO>> response =
        controller.getStaffFeedbacks(principal, EFeedbackStatus.IN_PROCESS, 0, 8);

    BddLogger.then("service is called with IN_PROCESS and result has 1 item");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().data()).hasSize(1);

    verify(declaredActivityService).getStaffFeedbacks(eq(EFeedbackStatus.IN_PROCESS), any());
  }

  @Test
  void getStaffFeedbacks_should_return_empty_list_when_staff_has_no_feedbacks() {
    BddLogger.given("A logged-in staff with no feedbacks");
    PagedResult<Feedback> emptyResult = new PagedResult<>(List.of(), new PageInfo(0, 8, 0));

    when(declaredActivityService.getStaffFeedbacks(isNull(), any())).thenReturn(emptyResult);

    BddLogger.when("getStaffFeedbacks is called");
    ResponseEntity<PagedResponse<FeedbackStaffListItemDTO>> response =
        controller.getStaffFeedbacks(principal, null, 0, 8);

    BddLogger.then("200 OK is returned with empty data and totalElements=0");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().data()).isEmpty();
    assertThat(response.getBody().page().totalElements()).isZero();

    verifyNoInteractions(feedbackStaffListItemDTOMapper);
  }
}
