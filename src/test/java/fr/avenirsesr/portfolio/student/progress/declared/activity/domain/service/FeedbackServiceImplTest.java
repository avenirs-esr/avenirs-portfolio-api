package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_DESCRIPTION_LENGTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.error.domain.exception.FieldValidationException;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.FeedbackData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.FeedbackInProcessException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.FeedbackMaximumIterationReachedException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.FeedbackNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.FeedbackRepository;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceImplTest {

  @Mock private FeedbackRepository feedbackRepository;
  @Mock private DeclaredActivityService declaredActivityService;
  @Mock private AssociationService associationService;
  @Mock private TraceService traceService;
  @Mock private DeclaredSkillProgressService declaredSkillProgressService;
  @Mock private LoggedInUserService loggedInUserService;
  @Mock private NotificationService notificationService;

  @InjectMocks private FeedbackServiceImpl service;

  @Captor private ArgumentCaptor<Feedback> feedbackCaptor;

  private Student student;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
  }

  @Nested
  class CreateFeedback {

    @Test
    void should_save_feedback_with_empty_associations_when_none_linked() {
      BddLogger.given(
          "A logged-in student, his declared activity with null reflexion and no associations");
      UUID declaredActivityId = UUID.randomUUID();
      Activity activity = ActivityFixture.create().toModel();
      DeclaredActivity declaredActivity =
          DeclaredActivity.create(
              UUID.randomUUID(), student, activity, null, null, null, null, null);

      when(declaredActivityService.fetchActivityAndCheckLoggedInStudentAuthorization(
              declaredActivityId))
          .thenReturn(declaredActivity);
      when(associationService.getAllOf(
              any(UUID.class), any(Class.class), ArgumentMatchers.<List<EAssociationType>>any()))
          .thenReturn(List.of());
      when(traceService.findAllTracesById(List.of())).thenReturn(List.of());
      when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of()))
          .thenReturn(List.of());
      when(feedbackRepository.findAllByDeclaredActivityId(declaredActivityId))
          .thenReturn(List.of());
      when(feedbackRepository.save(any(Feedback.class))).thenAnswer(i -> i.getArguments()[0]);

      BddLogger.when("createFeedback is called");
      service.createFeedback(declaredActivityId);

      BddLogger.then("A feedback with empty associations and null reflexion is saved");
      verify(feedbackRepository).save(feedbackCaptor.capture());
      Feedback captured = feedbackCaptor.getValue();
      assertThat(captured.getAssociatedTraces()).isEmpty();
      assertThat(captured.getAssociatedDeclaredSkills()).isEmpty();
      assertThat(captured.getReflexion()).isEmpty();
    }

    @Test
    void should_save_feedback_with_reflexion_and_associations_from_declared_activity() {
      BddLogger.given(
          "A logged-in student, his declared activity with a reflexion, 1 trace association and 1"
              + " skill association");
      UUID declaredActivityId = UUID.randomUUID();
      UUID traceId = UUID.randomUUID();
      UUID skillId = UUID.randomUUID();
      String reflexion = "Ma réflexion sur cette activité.";

      Activity activity = ActivityFixture.create().toModel();
      DeclaredActivity declaredActivity =
          DeclaredActivity.create(
              UUID.randomUUID(), student, activity, null, reflexion, null, null, null);

      var traceAssociation =
          mock(fr.avenirsesr.portfolio.association.domain.model.Association.class);
      var skillAssociation =
          mock(fr.avenirsesr.portfolio.association.domain.model.Association.class);
      Trace trace = mock(Trace.class);
      DeclaredSkillProgress skill = mock(DeclaredSkillProgress.class);

      when(declaredActivityService.fetchActivityAndCheckLoggedInStudentAuthorization(
              declaredActivityId))
          .thenReturn(declaredActivity);

      when(traceAssociation.getAssociationType())
          .thenReturn(EAssociationType.DECLARED_ACTIVITY_TRACE);
      when(traceAssociation.getId2()).thenReturn(traceId);
      when(skillAssociation.getAssociationType())
          .thenReturn(EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL);
      when(skillAssociation.getId2()).thenReturn(skillId);

      when(associationService.getAllOf(
              any(UUID.class), any(Class.class), ArgumentMatchers.<List<EAssociationType>>any()))
          .thenReturn(List.of(traceAssociation, skillAssociation));
      when(traceService.findAllTracesById(List.of(traceId))).thenReturn(List.of(trace));
      when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of(skillId)))
          .thenReturn(List.of(skill));
      when(feedbackRepository.findAllByDeclaredActivityId(declaredActivityId))
          .thenReturn(List.of());
      when(feedbackRepository.save(any(Feedback.class))).thenAnswer(i -> i.getArguments()[0]);

      BddLogger.when("createFeedback is called");
      service.createFeedback(declaredActivityId);

      BddLogger.then(
          "A feedback is saved with the activity's reflexion, 1 trace and 1 declared skill");
      verify(feedbackRepository).save(feedbackCaptor.capture());
      Feedback captured = feedbackCaptor.getValue();
      assertThat(captured.getReflexion().orElse(null)).isEqualTo(reflexion);
      assertThat(captured.getAssociatedTraces()).containsExactly(trace);
      assertThat(captured.getAssociatedDeclaredSkills()).containsExactly(skill);
    }

    @Test
    void should_throw_DeclaredActivityNotFoundException_when_activity_not_found() {
      BddLogger.given("A logged-in student and a non-existent declared activity ID");
      UUID declaredActivityId = UUID.randomUUID();

      when(declaredActivityService.fetchActivityAndCheckLoggedInStudentAuthorization(
              declaredActivityId))
          .thenThrow(new DeclaredActivityNotFoundException());

      BddLogger.when("createFeedback is called");

      BddLogger.then("A DeclaredActivityNotFoundException is thrown and nothing is saved");
      assertThatThrownBy(() -> service.createFeedback(declaredActivityId))
          .isInstanceOf(DeclaredActivityNotFoundException.class);

      verify(feedbackRepository, never()).save(any());
    }

    @Test
    void should_throw_UserNotAuthorizedException_when_activity_belongs_to_another_student() {
      BddLogger.given("A logged-in student and a declared activity belonging to another student");
      UUID declaredActivityId = UUID.randomUUID();

      when(declaredActivityService.fetchActivityAndCheckLoggedInStudentAuthorization(
              declaredActivityId))
          .thenThrow(new UserNotAuthorizedException());

      BddLogger.when("createFeedback is called");

      BddLogger.then("A UserNotAuthorizedException is thrown and nothing is saved");
      assertThatThrownBy(() -> service.createFeedback(declaredActivityId))
          .isInstanceOf(UserNotAuthorizedException.class);

      verify(feedbackRepository, never()).save(any());
    }

    @Test
    void should_throw_FieldValidationException_when_reflexion_exceeds_max_length() {
      BddLogger.given(
          "A logged-in student and his declared activity with a reflexion exceeding"
              + " RICH_DESCRIPTION_LENGTH");
      UUID declaredActivityId = UUID.randomUUID();
      String tooLongReflexion = "a".repeat(RICH_DESCRIPTION_LENGTH + 1);
      Activity activity = ActivityFixture.create().toModel();
      DeclaredActivity declaredActivity =
          DeclaredActivity.create(
              UUID.randomUUID(), student, activity, null, tooLongReflexion, null, null, null);

      when(declaredActivityService.fetchActivityAndCheckLoggedInStudentAuthorization(
              declaredActivityId))
          .thenReturn(declaredActivity);

      BddLogger.when("createFeedback is called");

      BddLogger.then("A FieldValidationException is thrown and nothing is saved");
      assertThatThrownBy(() -> service.createFeedback(declaredActivityId))
          .isInstanceOf(FieldValidationException.class);

      verify(feedbackRepository, never()).save(any());
    }

    @Test
    void should_throw_FeedbackInProcessException_when_last_feedback_is_IN_PROCESS() {
      BddLogger.given(
          "A logged-in student whose last feedback on the declared activity is IN_PROCESS");
      UUID declaredActivityId = UUID.randomUUID();
      Activity activity = ActivityFixture.create().toModel();
      DeclaredActivity declaredActivity =
          DeclaredActivity.create(
              UUID.randomUUID(), student, activity, null, null, null, null, null);

      Feedback inProcessFeedback =
          Feedback.toDomain(
              UUID.randomUUID(),
              Instant.now(),
              Instant.now(),
              declaredActivity,
              null,
              null,
              EFeedbackStatus.IN_PROCESS,
              1,
              List.of(),
              List.of());

      when(declaredActivityService.fetchActivityAndCheckLoggedInStudentAuthorization(
              declaredActivityId))
          .thenReturn(declaredActivity);
      when(feedbackRepository.findAllByDeclaredActivityId(declaredActivityId))
          .thenReturn(List.of(inProcessFeedback));

      BddLogger.when("createFeedback is called");

      BddLogger.then("A FeedbackInProcessException is thrown and nothing is saved");
      assertThatThrownBy(() -> service.createFeedback(declaredActivityId))
          .isInstanceOf(FeedbackInProcessException.class);

      verify(feedbackRepository, never()).save(any());
    }

    @Test
    void should_update_existing_feedback_when_last_feedback_is_NEW() {
      BddLogger.given(
          "A logged-in student whose last feedback is NEW, and the declared activity has a"
              + " reflexion and associations");
      UUID declaredActivityId = UUID.randomUUID();
      UUID traceId = UUID.randomUUID();
      UUID skillId = UUID.randomUUID();
      String updatedReflexion = "Nouvelle réflexion mise à jour.";

      Activity activity = ActivityFixture.create().toModel();
      DeclaredActivity declaredActivity =
          DeclaredActivity.create(
              UUID.randomUUID(), student, activity, null, updatedReflexion, null, null, null);

      UUID existingFeedbackId = UUID.randomUUID();
      Feedback existingFeedback =
          Feedback.toDomain(
              existingFeedbackId,
              Instant.now(),
              Instant.now(),
              declaredActivity,
              "Ancienne réflexion",
              null,
              EFeedbackStatus.NEW,
              1,
              List.of(),
              List.of());

      var traceAssociation =
          mock(fr.avenirsesr.portfolio.association.domain.model.Association.class);
      var skillAssociation =
          mock(fr.avenirsesr.portfolio.association.domain.model.Association.class);
      Trace trace = mock(Trace.class);
      DeclaredSkillProgress skill = mock(DeclaredSkillProgress.class);

      when(declaredActivityService.fetchActivityAndCheckLoggedInStudentAuthorization(
              declaredActivityId))
          .thenReturn(declaredActivity);
      when(feedbackRepository.findAllByDeclaredActivityId(declaredActivityId))
          .thenReturn(List.of(existingFeedback));

      when(traceAssociation.getAssociationType())
          .thenReturn(EAssociationType.DECLARED_ACTIVITY_TRACE);
      when(traceAssociation.getId2()).thenReturn(traceId);
      when(skillAssociation.getAssociationType())
          .thenReturn(EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL);
      when(skillAssociation.getId2()).thenReturn(skillId);
      when(associationService.getAllOf(
              any(UUID.class), any(Class.class), ArgumentMatchers.<List<EAssociationType>>any()))
          .thenReturn(List.of(traceAssociation, skillAssociation));
      when(traceService.findAllTracesById(List.of(traceId))).thenReturn(List.of(trace));
      when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of(skillId)))
          .thenReturn(List.of(skill));
      when(feedbackRepository.save(any(Feedback.class))).thenAnswer(i -> i.getArguments()[0]);

      BddLogger.when("createFeedback is called");
      service.createFeedback(declaredActivityId);

      BddLogger.then(
          "The existing feedback is updated and saved — same ID, updated reflexion, traces and"
              + " skills, status stays NEW");
      verify(feedbackRepository).save(feedbackCaptor.capture());
      Feedback saved = feedbackCaptor.getValue();
      assertThat(saved.getId()).isEqualTo(existingFeedbackId);
      assertThat(saved.getReflexion()).contains(updatedReflexion);
      assertThat(saved.getAssociatedTraces()).containsExactly(trace);
      assertThat(saved.getAssociatedDeclaredSkills()).containsExactly(skill);
      assertThat(saved.getStatus()).isEqualTo(EFeedbackStatus.NEW);
    }

    @Test
    void should_create_new_feedback_when_last_feedback_is_SUBMITTED_and_limit_not_reached() {
      BddLogger.given(
          "A logged-in student with 1 SUBMITTED feedback and feedbackAllowedIterations=2");
      UUID declaredActivityId = UUID.randomUUID();
      Activity activity = ActivityFixture.create().withFeedbackAllowedIterations(2).toModel();
      DeclaredActivity declaredActivity =
          DeclaredActivity.create(
              UUID.randomUUID(), student, activity, null, null, null, null, null);

      Feedback submittedFeedback =
          Feedback.toDomain(
              UUID.randomUUID(),
              Instant.now(),
              Instant.now(),
              declaredActivity,
              null,
              "Retour du formateur",
              EFeedbackStatus.SUBMITTED,
              1,
              List.of(),
              List.of());

      when(declaredActivityService.fetchActivityAndCheckLoggedInStudentAuthorization(
              declaredActivityId))
          .thenReturn(declaredActivity);
      when(feedbackRepository.findAllByDeclaredActivityId(declaredActivityId))
          .thenReturn(List.of(submittedFeedback));
      when(associationService.getAllOf(
              any(UUID.class), any(Class.class), ArgumentMatchers.<List<EAssociationType>>any()))
          .thenReturn(List.of());
      when(traceService.findAllTracesById(List.of())).thenReturn(List.of());
      when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of()))
          .thenReturn(List.of());
      when(feedbackRepository.save(any(Feedback.class))).thenAnswer(i -> i.getArguments()[0]);

      BddLogger.when("createFeedback is called");
      service.createFeedback(declaredActivityId);

      BddLogger.then(
          "A new feedback is created with a different ID from the submitted one and status NEW");
      verify(feedbackRepository).save(feedbackCaptor.capture());
      Feedback saved = feedbackCaptor.getValue();
      assertThat(saved.getId()).isNotEqualTo(submittedFeedback.getId());
      assertThat(saved.getStatus()).isEqualTo(EFeedbackStatus.NEW);
    }

    @Test
    void should_throw_FeedbackMaximumIterationReachedException_when_limit_is_reached() {
      BddLogger.given(
          "A logged-in student with 2 SUBMITTED feedbacks and feedbackAllowedIterations=2");
      UUID declaredActivityId = UUID.randomUUID();
      Activity activity = ActivityFixture.create().withFeedbackAllowedIterations(2).toModel();
      DeclaredActivity declaredActivity =
          DeclaredActivity.create(
              UUID.randomUUID(), student, activity, null, null, null, null, null);

      Feedback feedback1 =
          Feedback.toDomain(
              UUID.randomUUID(),
              Instant.now().minusSeconds(120),
              Instant.now().minusSeconds(120),
              declaredActivity,
              null,
              "Retour 1",
              EFeedbackStatus.SUBMITTED,
              1,
              List.of(),
              List.of());
      Feedback feedback2 =
          Feedback.toDomain(
              UUID.randomUUID(),
              Instant.now(),
              Instant.now(),
              declaredActivity,
              null,
              "Retour 2",
              EFeedbackStatus.SUBMITTED,
              1,
              List.of(),
              List.of());

      when(declaredActivityService.fetchActivityAndCheckLoggedInStudentAuthorization(
              declaredActivityId))
          .thenReturn(declaredActivity);
      when(feedbackRepository.findAllByDeclaredActivityId(declaredActivityId))
          .thenReturn(List.of(feedback1, feedback2));

      BddLogger.when("createFeedback is called");

      BddLogger.then("A FeedbackMaximumIterationReachedException is thrown and nothing is saved");
      assertThatThrownBy(() -> service.createFeedback(declaredActivityId))
          .isInstanceOf(FeedbackMaximumIterationReachedException.class);

      verify(feedbackRepository, never()).save(any());
    }
  }

  @Nested
  class GetFeedbackDetails {

    @Test
    void should_return_FeedbackData_with_feedback_visible_when_student_and_SUBMITTED() {
      BddLogger.given("A logged-in student requesting details of their own SUBMITTED feedback");
      UUID feedbackId = UUID.randomUUID();
      Activity activity = ActivityFixture.create().toModel();
      String feedbackText = "Très bon travail !";
      Feedback feedback =
          Feedback.toDomain(
              feedbackId,
              Instant.now(),
              Instant.now(),
              DeclaredActivity.create(
                  UUID.randomUUID(), student, activity, null, null, null, null, null),
              "Ma réflexion",
              feedbackText,
              EFeedbackStatus.SUBMITTED,
              1,
              List.of(),
              List.of());

      when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));
      when(loggedInUserService.getLoggedInUser()).thenReturn(student.getUser());

      BddLogger.when("getFeedbackDetails is called with STUDENT category");
      FeedbackData result = service.getFeedbackDetails(feedbackId, EUserCategory.STUDENT);

      BddLogger.then("FeedbackData is returned with the feedback text visible");
      assertThat(result.feedback()).isEqualTo(feedbackText);
      assertThat(result.status()).isEqualTo(EFeedbackStatus.SUBMITTED);
      assertThat(result.id()).isEqualTo(feedbackId);
    }

    @Test
    void should_return_FeedbackData_with_null_feedback_when_student_and_NEW() {
      BddLogger.given("A logged-in student requesting details of their own NEW feedback");
      UUID feedbackId = UUID.randomUUID();
      Activity activity = ActivityFixture.create().toModel();
      Feedback feedback =
          Feedback.toDomain(
              feedbackId,
              Instant.now(),
              Instant.now(),
              DeclaredActivity.create(
                  UUID.randomUUID(), student, activity, null, null, null, null, null),
              "Ma réflexion",
              "Retour non encore soumis",
              EFeedbackStatus.NEW,
              1,
              List.of(),
              List.of());

      when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));
      when(loggedInUserService.getLoggedInUser()).thenReturn(student.getUser());

      BddLogger.when("getFeedbackDetails is called with STUDENT category");
      FeedbackData result = service.getFeedbackDetails(feedbackId, EUserCategory.STUDENT);

      BddLogger.then("FeedbackData is returned with feedback set to null (not yet submitted)");
      assertThat(result.feedback()).isNull();
      assertThat(result.status()).isEqualTo(EFeedbackStatus.NEW);
    }

    @Test
    void should_throw_UserNotAuthorizedException_when_student_is_not_owner() {
      BddLogger.given("A logged-in student requesting details of another student's feedback");
      UUID feedbackId = UUID.randomUUID();
      Student anotherStudent = StudentFixture.create().toModel();
      Activity activity = ActivityFixture.create().toModel();
      Feedback feedback =
          Feedback.toDomain(
              feedbackId,
              Instant.now(),
              Instant.now(),
              DeclaredActivity.create(
                  UUID.randomUUID(), anotherStudent, activity, null, null, null, null, null),
              null,
              null,
              EFeedbackStatus.NEW,
              1,
              List.of(),
              List.of());

      when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));
      when(loggedInUserService.getLoggedInUser()).thenReturn(student.getUser());

      BddLogger.when("getFeedbackDetails is called with STUDENT category");

      BddLogger.then("A UserNotAuthorizedException is thrown");
      assertThatThrownBy(() -> service.getFeedbackDetails(feedbackId, EUserCategory.STUDENT))
          .isInstanceOf(UserNotAuthorizedException.class);
    }

    @Test
    void should_return_FeedbackData_with_feedback_when_staff_is_author() {
      BddLogger.given("A logged-in staff who is the author of the activity");
      UUID feedbackId = UUID.randomUUID();
      Activity activity = ActivityFixture.create().toModel();
      User authorUser = UserFixture.create().withId(activity.getAuthor().getId()).toModel();
      String feedbackText = "Excellent travail !";
      Feedback feedback =
          Feedback.toDomain(
              feedbackId,
              Instant.now(),
              Instant.now(),
              DeclaredActivity.create(
                  UUID.randomUUID(), student, activity, null, null, null, null, null),
              "Réflexion",
              feedbackText,
              EFeedbackStatus.IN_PROCESS,
              1,
              List.of(),
              List.of());

      when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));
      when(loggedInUserService.getLoggedInUser()).thenReturn(authorUser);

      BddLogger.when("getFeedbackDetails is called with STAFF category");
      FeedbackData result = service.getFeedbackDetails(feedbackId, EUserCategory.STAFF);

      BddLogger.then("FeedbackData is returned with the feedback text visible to staff");
      assertThat(result.feedback()).isEqualTo(feedbackText);
      assertThat(result.id()).isEqualTo(feedbackId);
    }

    @Test
    void should_throw_UserNotAuthorizedException_when_staff_is_not_author() {
      BddLogger.given("A logged-in user who is NOT the author of the activity");
      UUID feedbackId = UUID.randomUUID();
      Activity activity = ActivityFixture.create().toModel();
      User differentUser = UserFixture.create().toModel();
      Feedback feedback =
          Feedback.toDomain(
              feedbackId,
              Instant.now(),
              Instant.now(),
              DeclaredActivity.create(
                  UUID.randomUUID(), student, activity, null, null, null, null, null),
              null,
              null,
              EFeedbackStatus.IN_PROCESS,
              1,
              List.of(),
              List.of());

      when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));
      when(loggedInUserService.getLoggedInUser()).thenReturn(differentUser);

      BddLogger.when("getFeedbackDetails is called with STAFF category");

      BddLogger.then("A UserNotAuthorizedException is thrown");
      assertThatThrownBy(() -> service.getFeedbackDetails(feedbackId, EUserCategory.STAFF))
          .isInstanceOf(UserNotAuthorizedException.class);
    }

    @Test
    void should_throw_FeedbackNotFoundException_when_feedback_not_found() {
      BddLogger.given("A non-existent feedback ID");
      UUID feedbackId = UUID.randomUUID();

      when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.empty());
      when(loggedInUserService.getLoggedInUser()).thenReturn(student.getUser());

      BddLogger.when("getFeedbackDetails is called");

      BddLogger.then("A FeedbackNotFoundException is thrown");
      assertThatThrownBy(() -> service.getFeedbackDetails(feedbackId, EUserCategory.STUDENT))
          .isInstanceOf(FeedbackNotFoundException.class);
    }
  }

  @Nested
  class GetStudentFeedbackDetails {

    @Test
    void should_return_null_feedback_when_status_is_not_SUBMITTED() {
      BddLogger.given("A feedback with status IN_PROCESS and a feedback text set");
      Activity activity = ActivityFixture.create().toModel();
      Feedback feedback =
          Feedback.toDomain(
              UUID.randomUUID(),
              Instant.now(),
              Instant.now(),
              DeclaredActivity.create(
                  UUID.randomUUID(), student, activity, null, null, null, null, null),
              "Ma réflexion",
              "Retour du formateur",
              EFeedbackStatus.IN_PROCESS,
              1,
              List.of(),
              List.of());

      BddLogger.when("getStudentFeedbackDetails is called with the student's own user");
      FeedbackData result = service.getStudentFeedbackDetails(student.getUser(), feedback);

      BddLogger.then("FeedbackData is returned with feedback set to null");
      assertThat(result.feedback()).isNull();
      assertThat(result.reflexion()).isEqualTo("Ma réflexion");
      assertThat(result.status()).isEqualTo(EFeedbackStatus.IN_PROCESS);
    }

    @Test
    void should_return_feedback_text_when_status_is_SUBMITTED() {
      BddLogger.given("A SUBMITTED feedback with a feedback text");
      Activity activity = ActivityFixture.create().toModel();
      String feedbackText = "Excellent travail, continuez ainsi !";
      Feedback feedback =
          Feedback.toDomain(
              UUID.randomUUID(),
              Instant.now(),
              Instant.now(),
              DeclaredActivity.create(
                  UUID.randomUUID(), student, activity, null, null, null, null, null),
              "Ma réflexion",
              feedbackText,
              EFeedbackStatus.SUBMITTED,
              1,
              List.of(),
              List.of());

      BddLogger.when("getStudentFeedbackDetails is called with the student's own user");
      FeedbackData result = service.getStudentFeedbackDetails(student.getUser(), feedback);

      BddLogger.then("FeedbackData is returned with the feedback text visible");
      assertThat(result.feedback()).isEqualTo(feedbackText);
      assertThat(result.status()).isEqualTo(EFeedbackStatus.SUBMITTED);
    }

    @Test
    void should_throw_UserNotAuthorizedException_when_user_is_not_student() {
      BddLogger.given("A feedback belonging to one student but called with a different user");
      Activity activity = ActivityFixture.create().toModel();
      Feedback feedback =
          Feedback.toDomain(
              UUID.randomUUID(),
              Instant.now(),
              Instant.now(),
              DeclaredActivity.create(
                  UUID.randomUUID(), student, activity, null, null, null, null, null),
              null,
              null,
              EFeedbackStatus.NEW,
              1,
              List.of(),
              List.of());
      User differentUser = UserFixture.create().toModel();

      BddLogger.when("getStudentFeedbackDetails is called with a different user");

      BddLogger.then("A UserNotAuthorizedException is thrown");
      assertThatThrownBy(() -> service.getStudentFeedbackDetails(differentUser, feedback))
          .isInstanceOf(UserNotAuthorizedException.class);
    }
  }

  @Nested
  class UpdateFeedback {

    @Test
    void should_save_feedback_with_updated_text_when_user_is_author() {
      BddLogger.given("A logged-in staff who is the author of the activity and a valid feedback");
      UUID feedbackId = UUID.randomUUID();
      Activity activity = ActivityFixture.create().toModel();
      DeclaredActivity declaredActivity =
          DeclaredActivity.create(
              UUID.randomUUID(), student, activity, null, null, null, null, null);
      Feedback feedback =
          Feedback.toDomain(
              feedbackId,
              Instant.now(),
              Instant.now(),
              declaredActivity,
              null,
              null,
              EFeedbackStatus.IN_PROCESS,
              1,
              List.of(),
              List.of());
      String feedbackText = "Excellent travail, bravo !";
      User authorUser = UserFixture.create().withId(activity.getAuthor().getId()).toModel();

      when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));
      when(loggedInUserService.getLoggedInUser()).thenReturn(authorUser);
      when(feedbackRepository.save(any(Feedback.class))).thenAnswer(i -> i.getArguments()[0]);

      BddLogger.when("updateFeedback is called with the new feedback text");
      service.updateFeedback(feedbackId, feedbackText);

      BddLogger.then("The feedback is saved with the updated text");
      verify(feedbackRepository).save(feedbackCaptor.capture());
      Feedback saved = feedbackCaptor.getValue();
      assertThat(saved.getFeedback()).contains(feedbackText);
    }

    @Test
    void should_throw_FeedbackNotFoundException_when_feedback_does_not_exist() {
      BddLogger.given("A non-existent feedback ID");
      UUID feedbackId = UUID.randomUUID();

      when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.empty());

      BddLogger.when("updateFeedback is called");

      BddLogger.then("A FeedbackNotFoundException is thrown and nothing is saved");
      assertThatThrownBy(() -> service.updateFeedback(feedbackId, "some text"))
          .isInstanceOf(FeedbackNotFoundException.class);

      verify(feedbackRepository, never()).save(any());
    }

    @Test
    void should_throw_UserNotAuthorizedException_when_user_is_not_the_author() {
      BddLogger.given("A logged-in user who is NOT the author of the activity");
      UUID feedbackId = UUID.randomUUID();
      Activity activity = ActivityFixture.create().toModel();
      DeclaredActivity declaredActivity =
          DeclaredActivity.create(
              UUID.randomUUID(), student, activity, null, null, null, null, null);
      Feedback feedback =
          Feedback.toDomain(
              feedbackId,
              Instant.now(),
              Instant.now(),
              declaredActivity,
              null,
              null,
              EFeedbackStatus.IN_PROCESS,
              1,
              List.of(),
              List.of());
      User differentUser = UserFixture.create().toModel();

      when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));
      when(loggedInUserService.getLoggedInUser()).thenReturn(differentUser);

      BddLogger.when("updateFeedback is called");

      BddLogger.then("A UserNotAuthorizedException is thrown and nothing is saved");
      assertThatThrownBy(() -> service.updateFeedback(feedbackId, "some text"))
          .isInstanceOf(UserNotAuthorizedException.class);

      verify(feedbackRepository, never()).save(any());
    }

    @Test
    void should_throw_FieldValidationException_when_feedback_text_exceeds_max_length() {
      BddLogger.given("A valid feedback and a feedback text exceeding RICH_DESCRIPTION_LENGTH");
      UUID feedbackId = UUID.randomUUID();
      Activity activity = ActivityFixture.create().toModel();
      DeclaredActivity declaredActivity =
          DeclaredActivity.create(
              UUID.randomUUID(), student, activity, null, null, null, null, null);
      Feedback feedback =
          Feedback.toDomain(
              feedbackId,
              Instant.now(),
              Instant.now(),
              declaredActivity,
              null,
              null,
              EFeedbackStatus.IN_PROCESS,
              1,
              List.of(),
              List.of());
      String tooLongFeedback = "a".repeat(RICH_DESCRIPTION_LENGTH + 1);
      User authorUser = UserFixture.create().withId(activity.getAuthor().getId()).toModel();

      when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));
      when(loggedInUserService.getLoggedInUser()).thenReturn(authorUser);

      BddLogger.when("updateFeedback is called with a text that is too long");

      BddLogger.then("A FieldValidationException is thrown and nothing is saved");
      assertThatThrownBy(() -> service.updateFeedback(feedbackId, tooLongFeedback))
          .isInstanceOf(FieldValidationException.class);

      verify(feedbackRepository, never()).save(any());
    }
  }
}
