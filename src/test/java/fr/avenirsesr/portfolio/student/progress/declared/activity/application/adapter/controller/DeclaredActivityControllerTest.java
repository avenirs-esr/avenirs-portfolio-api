package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.association.application.adapter.mapper.AssociationSearchResultDTOMapper;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.CreationResponse;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.FeedbackDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.SubscribeDeclaredActivityRequest;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityAssociationsDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityDetailsDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityViewDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.FeedbackDetailsDTOMapper;
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
}
