package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.association.application.adapter.mapper.AssociationSearchResultDTOMapper;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.FeedbackDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityAssociationsDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityDetailsDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityViewDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.FeedbackDetailsDTOMapper;
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
