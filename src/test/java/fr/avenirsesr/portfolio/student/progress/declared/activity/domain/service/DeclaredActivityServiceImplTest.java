package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.service;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_TEXT_LENGTH;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.activity.domain.exception.ActivityNotFoundException;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.association.domain.exception.MaximumAssociationReachedException;
import fr.avenirsesr.portfolio.association.domain.model.Association;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.association.domain.service.AssociationSearchHelper;
import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.error.domain.exception.BusinessException;
import fr.avenirsesr.portfolio.common.error.domain.exception.FieldValidationException;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityAlreadyExistException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityAlreadyFinishedException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityDatesException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityHasNotStartedException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityStartDateBeforeSubscriptionException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.FeedbackInProcessException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.FeedbackMaximumIterationReachedException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.FeedbackRepository;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeclaredActivityServiceImplTest {

  @Mock private DeclaredActivityRepository declaredActivityRepository;
  @Mock private ActivityService activityService;

  @Mock private AssociationService associationService;
  @Mock private AssociationSearchHelper associationSearchHelper;
  @Mock private TraceService traceService;
  @Mock private DeclaredSkillProgressService declaredSkillProgressService;
  @Mock private LoggedInUserService loggedInUserService;
  @Mock private FeedbackRepository feedbackRepository;

  @InjectMocks private DeclaredActivityServiceImpl service;

  private DeclaredActivityService declaredActivityService;

  @Captor private ArgumentCaptor<DeclaredActivity> activityCaptor;
  @Captor private ArgumentCaptor<Feedback> feedbackCaptor;

  private Student student;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
    declaredActivityService =
        new DeclaredActivityServiceImpl(
            declaredActivityRepository,
            activityService,
            traceService,
            declaredSkillProgressService,
            associationService,
            associationSearchHelper,
            loggedInUserService,
            feedbackRepository);
  }

  @Test
  void subscribe_should_create_and_save_when_activity_exists_and_not_already_subscribed() {
    BddLogger.given("A logged-in student and an existing activity he is not subscribed to");
    UUID activityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findByActivity(student, activity)).thenReturn(Optional.empty());
    when(activityService.getActivityById(activityId)).thenReturn(activity);

    BddLogger.when("He tries to subscribe to this activity without dates");
    service.subscribe(activityId, null, null);

    BddLogger.then("The student subscribed to the activity");
    verify(declaredActivityRepository).save(activityCaptor.capture());
    DeclaredActivity savedActivity = activityCaptor.getValue();

    assertThat(savedActivity.getStudent()).isEqualTo(student);
    assertThat(savedActivity.getActivity()).isEqualTo(activity);
    assertThat(savedActivity.getStartedAt()).isEmpty();
    assertThat(savedActivity.getStartDate()).isNull();
    assertThat(savedActivity.getEndDate()).isNull();
  }

  @Test
  void subscribe_should_throw_ActivityNotFoundException_when_activity_does_not_exist() {
    BddLogger.given("A logged-in student and a non-existent activity ID");
    UUID activityId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(activityService.getActivityById(activityId)).thenThrow(new ActivityNotFoundException());

    BddLogger.when("He tries to subscribe to this activity");

    BddLogger.then("An ActivityNotFoundException is thrown and nothing is saved");
    assertThatThrownBy(() -> service.subscribe(activityId, null, null))
        .isInstanceOf(ActivityNotFoundException.class);

    verify(declaredActivityRepository, never()).findByActivity(any(), any());
    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void subscribe_should_throw_DeclaredActivityAlreadyExistException_when_already_subscribed() {
    BddLogger.given("A logged-in student already subscribed to an existing activity");
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(activityService.getActivityById(activity.getId())).thenReturn(activity);
    when(declaredActivityRepository.findByActivity(student, activity))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He tries to subscribe to it again");

    BddLogger.then("A DeclaredActivityAlreadyExistException is thrown");
    assertThatThrownBy(() -> service.subscribe(activity.getId(), null, null))
        .isInstanceOf(DeclaredActivityAlreadyExistException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void subscribe_should_throw_DeclaredActivityDatesException_when_only_one_date_provided() {
    BddLogger.given("A logged-in student and a valid activity, but only startDate is provided");
    UUID activityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();
    LocalDate startDate = LocalDate.now();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(activityService.getActivityById(activityId)).thenReturn(activity);

    BddLogger.when("He tries to subscribe with incomplete dates");

    BddLogger.then("A DeclaredActivityDatesException is thrown");
    assertThatThrownBy(() -> service.subscribe(activityId, startDate, null))
        .isInstanceOf(DeclaredActivityDatesException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void subscribe_should_throw_FieldValidationException_when_endDate_before_startDate() {
    BddLogger.given("A logged-in student and dates where endDate is before startDate");
    UUID activityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();
    LocalDate startDate = LocalDate.now().plusDays(5);
    LocalDate endDate = LocalDate.now().plusDays(2);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(activityService.getActivityById(activityId)).thenReturn(activity);

    BddLogger.when("He tries to subscribe with inconsistent dates");

    BddLogger.then("A FieldValidationException is thrown");
    assertThatThrownBy(() -> service.subscribe(activityId, startDate, endDate))
        .isInstanceOf(FieldValidationException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void
      subscribe_should_throw_DeclaredActivityStartDateBeforeSubscriptionException_when_startDate_is_in_past() {
    BddLogger.given("A logged-in student and a startDate that is before today");
    UUID activityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();
    LocalDate startDate = LocalDate.now().minusDays(10);
    LocalDate endDate = LocalDate.now().plusDays(5);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(activityService.getActivityById(activityId)).thenReturn(activity);

    BddLogger.when("He tries to subscribe with a past startDate");

    BddLogger.then("A DeclaredActivityStartDateBeforeSubscriptionException is thrown");
    assertThatThrownBy(() -> service.subscribe(activityId, startDate, endDate))
        .isInstanceOf(DeclaredActivityStartDateBeforeSubscriptionException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void
      unsubscribeMultiple_shouldRemoveDeclaredActivities_whenOwnedByStudent_and_all_activityIds_found() {
    BddLogger.given("Valid declared activities (found by activityIds) owned by the student");

    var declaredActivity1 = mock(DeclaredActivity.class);
    var declaredActivity2 = mock(DeclaredActivity.class);

    var activity1 = mock(Activity.class);
    var activity2 = mock(Activity.class);

    var idActivity1 = UUID.randomUUID();
    var idActivity2 = UUID.randomUUID();
    var activityIds = List.of(idActivity1, idActivity2);

    when(declaredActivity1.getId()).thenReturn(idActivity1);
    when(declaredActivity2.getId()).thenReturn(idActivity2);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    // repository returns declared activities found by (activityIds + student + graph)
    when(declaredActivityRepository.findAllByActivityIdAndStudent(
            eq(activityIds), eq(student), any(FetchGraph.class)))
        .thenReturn(List.of(declaredActivity1, declaredActivity2));

    // each declared activity has an activity with an id
    when(declaredActivity1.getActivity()).thenReturn(activity1);
    when(declaredActivity2.getActivity()).thenReturn(activity2);
    when(activity1.getId()).thenReturn(idActivity1);
    when(activity2.getId()).thenReturn(idActivity2);
    doNothing().when(associationService).deleteAllOf(activityIds, DeclaredActivity.class);

    BddLogger.when("He requests to unsubscribe from these activities");
    declaredActivityService.unsubscribeMultiple(activityIds);

    BddLogger.then("The student is removed from all activities.");
    verify(declaredActivityRepository)
        .removeAllFromDatabase(List.of(declaredActivity1, declaredActivity2));
    verify(associationService)
        .deleteAllOf(
            List.of(declaredActivity1.getId(), declaredActivity2.getId()), DeclaredActivity.class);
  }

  @Test
  void
      unsubscribeMultiple_shouldThrowDeclaredActivityNotFoundException_when_some_activityIds_not_found_for_student() {
    BddLogger.given("Some activityIds are not subscribed by the student");

    var declaredActivity1 = mock(DeclaredActivity.class);
    var activity1 = mock(Activity.class);

    var idActivity1 = UUID.randomUUID();
    var idActivity2Missing = UUID.randomUUID();
    var activityIds = List.of(idActivity1, idActivity2Missing);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    when(declaredActivityRepository.findAllByActivityIdAndStudent(
            eq(activityIds), eq(student), any(FetchGraph.class)))
        .thenReturn(List.of(declaredActivity1));

    when(declaredActivity1.getActivity()).thenReturn(activity1);
    when(activity1.getId()).thenReturn(idActivity1);

    BddLogger.when("He requests to unsubscribe from these activities");

    BddLogger.then("A DeclaredActivityNotFoundException is thrown and nothing is removed");
    assertThatThrownBy(() -> declaredActivityService.unsubscribeMultiple(activityIds))
        .isInstanceOf(DeclaredActivityNotFoundException.class);

    verify(declaredActivityRepository, never()).removeAllFromDatabase(anyList());
  }

  @Test
  void finish_should_update_finishedAt_and_save_when_valid() {
    BddLogger.given("A logged-in student and his existing started declared activity");
    UUID declaredActivityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            UUID.randomUUID(), student, activity, Instant.now(), null, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivityRepository.save(any(DeclaredActivity.class)))
        .thenAnswer(i -> i.getArguments()[0]);

    BddLogger.when("He tries to finish this activity");
    DeclaredActivity result = service.finish(declaredActivityId);

    BddLogger.then("The declared activity is marked as finished and saved");
    assertThat(result.getFinishedAt()).isPresent();
    verify(declaredActivityRepository).save(declaredActivity);
  }

  @Test
  void finish_should_throw_DeclaredActivityHasNotStartedException_when_not_started() {
    BddLogger.given("A logged-in student and a declared activity that has not started yet");
    UUID declaredActivityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He tries to finish this activity");

    BddLogger.then("A DeclaredActivityHasNotStartedException is thrown");
    assertThatThrownBy(() -> service.finish(declaredActivityId))
        .isInstanceOf(DeclaredActivityHasNotStartedException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void finish_should_throw_DeclaredActivityNotFoundException_when_not_found() {
    BddLogger.given("A logged-in student and a non-existent declared activity ID");
    UUID declaredActivityId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId)).thenReturn(Optional.empty());

    BddLogger.when("He tries to finish this activity");

    BddLogger.then("A DeclaredActivityNotFoundException is thrown");
    assertThatThrownBy(() -> service.finish(declaredActivityId))
        .isInstanceOf(DeclaredActivityNotFoundException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void finish_should_throw_UserNotAuthorizedException_when_belonging_to_another_student() {
    BddLogger.given("A logged-in student and a declared activity belonging to another student");
    UUID declaredActivityId = UUID.randomUUID();

    Student anotherStudent = StudentFixture.create().toModel();
    Activity activity = ActivityFixture.create().toModel();

    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            UUID.randomUUID(), anotherStudent, activity, null, null, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He tries to finish this activity");

    BddLogger.then("A UserNotAuthorizedException is thrown");
    assertThatThrownBy(() -> service.finish(declaredActivityId))
        .isInstanceOf(UserNotAuthorizedException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void finish_should_throw_DeclaredActivityAlreadyFinishedException_when_already_finished() {
    BddLogger.given("A logged-in student and his already finished declared activity");
    UUID declaredActivityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    declaredActivity.setStartedAt(Instant.now());
    declaredActivity.setFinishedAt(Instant.now());

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He tries to finish this activity again");

    BddLogger.then("A DeclaredActivityAlreadyFinishedException is thrown");
    assertThatThrownBy(() -> service.finish(declaredActivityId))
        .isInstanceOf(DeclaredActivityAlreadyFinishedException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void shouldUpdateReflectionSuccessfully() {
    BddLogger.given("Un DeclaredActivity existant récupéré depuis le repository");

    UUID activityId = UUID.randomUUID();

    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
    Activity activity = ActivityFixture.create().toModel();
    Student student = mock(Student.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivity.getStudent()).thenReturn(student);
    when(declaredActivity.getActivity()).thenReturn(activity);

    when(declaredActivityRepository.findById(activityId)).thenReturn(Optional.of(declaredActivity));

    String reflection = "Nouvelle réflexion";
    BddLogger.and("Un body reflection contenant : " + reflection);

    when(declaredActivity.getStartedAt()).thenReturn(Optional.empty());
    BddLogger.when("Le service updateReflection est appelé");
    declaredActivityService.updateReflection(activityId, reflection);

    BddLogger.then("La reflection doit être mise à jour");
    verify(declaredActivity).setReflection(reflection);

    BddLogger.and("La DeclaredActivity doit être persistée via le repository");
    verify(declaredActivityRepository).save(declaredActivity);

    BddLogger.and("Le statut doit être IN_PROGRESS");
    when(declaredActivity.getStatus()).thenReturn(EDeclaredActivityStatus.IN_PROGRESS);
    Assertions.assertEquals(EDeclaredActivityStatus.IN_PROGRESS, declaredActivity.getStatus());
  }

  @Test
  void getDeclaredActivityDetails_should_return_declared_activity_when_owned_by_student() {
    BddLogger.given("A logged-in student and an existing declared activity belonging to him");
    UUID declaredActivityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He requests declared activity details");
    DeclaredActivity result = service.getDeclaredActivityDetails(declaredActivityId);

    BddLogger.then("The declared activity is returned and no save is performed");
    assertThat(result).isSameAs(declaredActivity);
    verify(declaredActivityRepository).findById(eq(declaredActivityId), any(FetchGraph.class));
    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void getDeclaredActivityDetails_should_throw_DeclaredActivityNotFoundException_when_not_found() {
    BddLogger.given("A logged-in student and a non-existent declared activity ID");
    UUID declaredActivityId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.empty());

    BddLogger.when("He requests declared activity details");

    BddLogger.then("A DeclaredActivityNotFoundException is thrown");
    assertThatThrownBy(() -> service.getDeclaredActivityDetails(declaredActivityId))
        .isInstanceOf(DeclaredActivityNotFoundException.class);

    verify(declaredActivityRepository).findById(eq(declaredActivityId), any(FetchGraph.class));
    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void
      getDeclaredActivityDetails_should_throw_UserNotAuthorizedException_when_belonging_to_another_student() {
    BddLogger.given("A logged-in student and a declared activity belonging to another student");
    UUID declaredActivityId = UUID.randomUUID();

    Student anotherStudent = StudentFixture.create().toModel();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            UUID.randomUUID(), anotherStudent, activity, null, null, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He requests declared activity details");

    BddLogger.then("A UserNotAuthorizedException is thrown");
    assertThatThrownBy(() -> service.getDeclaredActivityDetails(declaredActivityId))
        .isInstanceOf(UserNotAuthorizedException.class);

    verify(declaredActivityRepository).findById(eq(declaredActivityId), any(FetchGraph.class));
    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void shouldUpdatePeriodSuccessfully() {
    // Given
    UUID declaredActivityId = UUID.randomUUID();
    var student = mock(Student.class);
    var declaredActivity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(student);
    when(declaredActivity.getCreatedAt()).thenReturn(Instant.now());

    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(10);

    // When
    BddLogger.when("The service is called with valid dates.");
    declaredActivityService.updateDeclaredActivityDates(declaredActivityId, startDate, endDate);

    // Then
    BddLogger.then("The DeclaredActivity receives the new dates.");
    verify(declaredActivity).setStartDate(startDate);
    verify(declaredActivity).setEndDate(endDate);

    verify(declaredActivityRepository).save(declaredActivity);
  }

  @Test
  void shouldThrowWhenEndDateBeforeStartDate() {
    UUID declaredActivityId = UUID.randomUUID();
    var student = mock(Student.class);
    var declaredActivity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(student);
    when(declaredActivity.getCreatedAt()).thenReturn(Instant.now());

    LocalDate startDate = LocalDate.now().plusDays(10);
    LocalDate endDate = LocalDate.now().plusDays(1);

    FieldValidationException ex =
        Assertions.assertThrows(
            FieldValidationException.class,
            () ->
                declaredActivityService.updateDeclaredActivityDates(
                    declaredActivityId, startDate, endDate));

    Assertions.assertEquals(EErrorCode.END_DATE_BEFORE_START_DATE, ex.getErrorCode());
  }

  @Test
  void shouldThrowWhenStartDateBeforeInscriptionDate() {
    UUID declaredActivityId = UUID.randomUUID();
    var student = mock(Student.class);
    var declaredActivity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(student);
    when(declaredActivity.getCreatedAt()).thenReturn(Instant.now());

    LocalDate startDate = LocalDate.now().minusDays(1);
    LocalDate endDate = startDate.plusDays(5);

    BddLogger.when("The service is called with a startDate before the registration date.");

    BusinessException ex =
        Assertions.assertThrows(
            BusinessException.class,
            () ->
                declaredActivityService.updateDeclaredActivityDates(
                    declaredActivityId, startDate, endDate));

    Assertions.assertEquals(
        EErrorCode.DECLARED_ACTIVITY_START_DATE_BEFORE_SUBSCRIPTION, ex.getErrorCode());
  }

  @Test
  void deleteAssociations_should_delete_when_associations_belong_to_declaredActivity() {

    BddLogger.given("A logged-in student and a declared activity with associated traces");

    UUID declaredActivityId = UUID.randomUUID();
    UUID associationId1 = UUID.randomUUID();
    UUID associationId2 = UUID.randomUUID();

    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
    Trace trace1 = mock(Trace.class);
    Trace trace2 = mock(Trace.class);

    Association association1 = mock(Association.class);
    Association association2 = mock(Association.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));

    when(declaredActivity.getStudent()).thenReturn(student);

    when(associationService.getAllOf(
            declaredActivity.getId(),
            DeclaredActivity.class,
            List.of(EAssociationType.DECLARED_ACTIVITY_TRACE)))
        .thenReturn(List.of(association1, association2));

    when(association1.getId()).thenReturn(associationId1);
    when(association2.getId()).thenReturn(associationId2);

    BddLogger.when("deleteAssociations is called");

    service.deleteAssociations(declaredActivityId, List.of(associationId1, associationId1));

    BddLogger.then("deleteAllByIds should be called");

    verify(associationService).deleteAllByIds(List.of(associationId1, associationId1));
  }

  @Test
  void deleteAssociations_should_throw_when_declaredActivity_not_found() {

    BddLogger.given("DeclaredActivity does not exist");

    UUID declaredActivityId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.empty());

    BddLogger.when("deleteAssociations is called");

    BddLogger.then("DeclaredActivityNotFoundException is thrown");

    assertThatThrownBy(
            () -> service.deleteAssociations(declaredActivityId, List.of(UUID.randomUUID())))
        .isInstanceOf(DeclaredActivityNotFoundException.class);

    verify(associationService, never()).deleteAllByIds(anyList());
  }

  @Test
  void deleteAssociations_should_throw_when_activity_belongs_to_other_student() {

    BddLogger.given("DeclaredActivity belongs to another student");

    UUID declaredActivityId = UUID.randomUUID();

    Student otherStudent = mock(Student.class);
    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));

    when(declaredActivity.getStudent()).thenReturn(otherStudent);

    BddLogger.when("deleteAssociations is called");

    BddLogger.then("UserNotAuthorizedException is thrown");

    assertThatThrownBy(
            () -> service.deleteAssociations(declaredActivityId, List.of(UUID.randomUUID())))
        .isInstanceOf(UserNotAuthorizedException.class);

    verify(associationService, never()).deleteAllByIds(anyList());
  }

  @Test
  void searchTracesForAssociation_should_return_traces_with_correct_disabled_status() {

    BddLogger.given(
        "A logged-in student, a declared activity owned by him, 2 already-associated traces and 1"
            + " not");

    UUID declaredActivityId = UUID.randomUUID();
    UUID traceId1 = UUID.randomUUID();
    UUID traceId2 = UUID.randomUUID();
    UUID traceId3 = UUID.randomUUID();

    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(student);

    var pageInfo = new PageInfo(0, 10, 3);
    var pageCriteria = new PageCriteria(0, 10);

    var expectedResults =
        List.of(
            new AssociationSearchResultData(traceId1, "Trace 1", null, true),
            new AssociationSearchResultData(traceId2, "Trace 2", null, true),
            new AssociationSearchResultData(traceId3, "Trace 3", null, false));

    when(associationSearchHelper.searchForAssociation(
            eq(declaredActivityId),
            eq(DeclaredActivity.class),
            eq(EAssociationType.DECLARED_ACTIVITY_TRACE),
            any(),
            any(),
            any(),
            any(),
            any(),
            any()))
        .thenReturn(new PagedResult<>(expectedResults, pageInfo));

    BddLogger.when("searchTracesForAssociation is called");
    PagedResult<AssociationSearchResultData> result =
        service.searchTracesForAssociation(declaredActivityId, "keyword", pageCriteria, null);

    BddLogger.then("The result contains 3 items: 2 with disabled=true, 1 with disabled=false");
    assertThat(result.content()).hasSize(3);
    assertThat(result.pageInfo()).isEqualTo(pageInfo);
    assertThat(result.content().get(0).disabled()).isTrue();
    assertThat(result.content().get(1).disabled()).isTrue();
    assertThat(result.content().get(2).disabled()).isFalse();
  }

  @Test
  void searchTracesForAssociation_should_return_empty_result_when_no_traces() {

    BddLogger.given("A logged-in student and a declared activity with no traces returned");

    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(student);

    var pageInfo = new PageInfo(0, 10, 0);
    var pageCriteria = new PageCriteria(0, 10);

    when(associationSearchHelper.searchForAssociation(
            eq(declaredActivityId),
            eq(DeclaredActivity.class),
            eq(EAssociationType.DECLARED_ACTIVITY_TRACE),
            any(),
            any(),
            any(),
            any(),
            any(),
            any()))
        .thenReturn(new PagedResult<>(List.of(), pageInfo));

    BddLogger.when("searchTracesForAssociation is called");
    PagedResult<AssociationSearchResultData> result =
        service.searchTracesForAssociation(declaredActivityId, null, pageCriteria, null);

    BddLogger.then("The result is empty");
    assertThat(result.content()).isEmpty();
    assertThat(result.pageInfo()).isEqualTo(pageInfo);
  }

  @Test
  void searchTracesForAssociation_should_throw_DeclaredActivityNotFoundException_when_not_found() {

    BddLogger.given("A logged-in student and a non-existent declared activity");

    UUID declaredActivityId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.empty());

    BddLogger.when("searchTracesForAssociation is called");

    BddLogger.then("A DeclaredActivityNotFoundException is thrown");
    assertThatThrownBy(
            () ->
                service.searchTracesForAssociation(
                    declaredActivityId, null, new PageCriteria(0, 10), null))
        .isInstanceOf(DeclaredActivityNotFoundException.class);

    verify(traceService, never()).getTracesView(any(), any(), any(), any());
  }

  @Test
  void
      searchTracesForAssociation_should_throw_UserNotAuthorizedException_when_belonging_to_another_student() {

    BddLogger.given("A logged-in student and a declared activity belonging to another student");

    UUID declaredActivityId = UUID.randomUUID();
    Student anotherStudent = StudentFixture.create().toModel();
    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(anotherStudent);

    BddLogger.when("searchTracesForAssociation is called");

    BddLogger.then("A UserNotAuthorizedException is thrown");
    assertThatThrownBy(
            () ->
                service.searchTracesForAssociation(
                    declaredActivityId, null, new PageCriteria(0, 10), null))
        .isInstanceOf(UserNotAuthorizedException.class);

    verify(traceService, never()).getTracesView(any(), any(), any(), any());
  }

  @Test
  void searchTracesForAssociation_should_pass_isAssociated_filter_and_keyword_to_traceService() {

    BddLogger.given("A logged-in student and a declared activity owned by him");

    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(student);

    var pageCriteria = new PageCriteria(1, 5);

    when(associationSearchHelper.searchForAssociation(
            eq(declaredActivityId),
            eq(DeclaredActivity.class),
            eq(EAssociationType.DECLARED_ACTIVITY_TRACE),
            any(),
            any(),
            any(),
            any(),
            any(),
            any()))
        .thenReturn(new PagedResult<>(List.of(), new PageInfo(1, 5, 0)));

    BddLogger.when(
        "searchTracesForAssociation is called with keyword='java' and isAssociated=true");
    service.searchTracesForAssociation(declaredActivityId, "java", pageCriteria, true);

    BddLogger.then(
        "traceService.getTracesView is called with the keyword, isAssociated=true in the filter,"
            + " null dateFilter and correct pageCriteria");
    var traceFilterCaptor = ArgumentCaptor.forClass(TraceFilter.class);
    verify(traceService)
        .getTracesView(eq("java"), traceFilterCaptor.capture(), eq(null), eq(pageCriteria));

    assertThat(traceFilterCaptor.getValue().isAssociated()).isTrue();
    assertThat(traceFilterCaptor.getValue().fileTypes()).isNull();
    assertThat(traceFilterCaptor.getValue().skillIds()).isNull();
    assertThat(traceFilterCaptor.getValue().statuses()).isNull();
  }

  @Test
  void deleteAssociations_should_throw_when_ids_not_associated() {

    BddLogger.given("Trace ids not associated to declaredActivity");

    UUID declaredActivityId = UUID.randomUUID();
    UUID traceId1 = UUID.randomUUID();
    UUID traceIdNotAssociated = UUID.randomUUID();

    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
    Trace trace1 = mock(Trace.class);

    Association association1 = mock(Association.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));

    when(declaredActivity.getStudent()).thenReturn(student);

    when(associationService.getAllOf(
            declaredActivity.getId(),
            DeclaredActivity.class,
            List.of(EAssociationType.DECLARED_ACTIVITY_TRACE)))
        .thenReturn(List.of(association1));

    BddLogger.when("deleteAssociations is called with non associated id");

    BddLogger.then("UserNotAuthorizedException is thrown");

    assertThatThrownBy(
            () ->
                service.deleteAssociations(
                    declaredActivityId, List.of(traceId1, traceIdNotAssociated)))
        .isInstanceOf(UserNotAuthorizedException.class);

    verify(associationService, never()).deleteAllByIds(anyList());
  }

  @Test
  void searchDeclaredActivity_should_return_paged_result_from_repository_with_correct_graph() {
    BddLogger.given("A logged-in student, a keyword and page criteria");
    String keyword = "recherche";
    PageCriteria pageCriteria = new PageCriteria(0, 10);
    PageInfo pageInfo = new PageInfo(0, 10, 1);

    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
    PagedResult<DeclaredActivity> expectedResult =
        new PagedResult<>(List.of(declaredActivity), pageInfo);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    ArgumentCaptor<FetchGraph> graphCaptor = ArgumentCaptor.forClass(FetchGraph.class);

    when(declaredActivityRepository.findAllByStudent(
            eq(student), eq(keyword), eq(pageCriteria), graphCaptor.capture()))
        .thenReturn(expectedResult);

    BddLogger.when("He searches for declared activities");
    PagedResult<DeclaredActivity> result = service.searchDeclaredActivity(keyword, pageCriteria);

    BddLogger.then(
        "It should return the paginated result from the repository with the correct FetchGraph");
    assertThat(result).isEqualTo(expectedResult);

    assertThat(graphCaptor.getValue().children()).containsKey("activity");
  }

  @Test
  void getDeclaredActivityAssociations_should_return_traces_and_declaredSkills_when_valid() {
    BddLogger.given(
        "A logged-in student, a declared activity owned by him with trace and declared skill"
            + " associations");

    UUID declaredActivityId = UUID.randomUUID();

    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

    UUID traceId = UUID.randomUUID();
    UUID skillId = UUID.randomUUID();

    Association traceAssociation = mock(Association.class);
    Association skillAssociation = mock(Association.class);

    Trace trace = mock(Trace.class);
    DeclaredSkillProgress skill = mock(DeclaredSkillProgress.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(student);
    when(declaredActivity.getId()).thenReturn(declaredActivityId);

    when(associationService.getAllOf(
            declaredActivityId,
            DeclaredActivity.class,
            List.of(
                EAssociationType.DECLARED_ACTIVITY_TRACE,
                EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL)))
        .thenReturn(List.of(traceAssociation, skillAssociation));

    when(traceAssociation.getAssociationType())
        .thenReturn(EAssociationType.DECLARED_ACTIVITY_TRACE);
    when(traceAssociation.getId()).thenReturn(UUID.randomUUID());
    when(traceAssociation.getId2()).thenReturn(traceId);

    when(skillAssociation.getAssociationType())
        .thenReturn(EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL);
    when(skillAssociation.getId()).thenReturn(UUID.randomUUID());
    when(skillAssociation.getId2()).thenReturn(skillId);

    when(traceService.findAllTracesById(List.of(traceId))).thenReturn(List.of(trace));
    when(trace.getId()).thenReturn(traceId);

    when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of(skillId)))
        .thenReturn(List.of(skill));
    when(skill.getId()).thenReturn(skillId);

    BddLogger.when("getDeclaredActivityAssociations is called");
    var result = service.getDeclaredActivityAssociations(declaredActivityId);

    BddLogger.then("Both trace and declared skill associations are returned");
    assertThat(result.traceAssociations()).hasSize(1);
    assertThat(result.declaredSkillAssociations()).hasSize(1);
  }

  @Test
  void getDeclaredActivityAssociations_should_return_empty_lists_when_no_associations() {
    BddLogger.given("A declared activity with no associations");

    UUID declaredActivityId = UUID.randomUUID();
    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(student);
    when(declaredActivity.getId()).thenReturn(declaredActivityId);

    when(associationService.getAllOf(any(), any(), any())).thenReturn(List.of());

    when(traceService.findAllTracesById(List.of())).thenReturn(List.of());
    when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of()))
        .thenReturn(List.of());

    BddLogger.when("getDeclaredActivityAssociations is called");
    var result = service.getDeclaredActivityAssociations(declaredActivityId);

    BddLogger.then("Empty lists are returned");
    assertThat(result.traceAssociations()).isEmpty();
    assertThat(result.declaredSkillAssociations()).isEmpty();
  }

  @Test
  void getDeclaredActivityAssociations_should_throw_when_trace_not_found_in_fetched_data() {
    BddLogger.given("An association referencing a trace that is not returned by traceService");

    UUID declaredActivityId = UUID.randomUUID();
    UUID traceId = UUID.randomUUID();

    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
    Association traceAssociation = mock(Association.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(student);
    when(declaredActivity.getId()).thenReturn(declaredActivityId);

    when(associationService.getAllOf(any(), any(), any())).thenReturn(List.of(traceAssociation));

    when(traceAssociation.getAssociationType())
        .thenReturn(EAssociationType.DECLARED_ACTIVITY_TRACE);
    when(traceAssociation.getId2()).thenReturn(traceId);

    when(traceService.findAllTracesById(List.of(traceId))).thenReturn(List.of());

    when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(any()))
        .thenReturn(List.of());

    BddLogger.when("getDeclaredActivityAssociations is called");

    BddLogger.then("TraceNotFoundException is thrown");
    assertThatThrownBy(() -> service.getDeclaredActivityAssociations(declaredActivityId))
        .isInstanceOf(TraceNotFoundException.class);
  }

  @Test
  void getDeclaredActivityAssociations_should_throw_when_declaredSkill_not_found_in_fetched_data() {
    BddLogger.given(
        "An association referencing a declared skill that is not returned by"
            + " declaredSkillProgressService");

    UUID declaredActivityId = UUID.randomUUID();
    UUID skillId = UUID.randomUUID();

    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
    Association skillAssociation = mock(Association.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(student);
    when(declaredActivity.getId()).thenReturn(declaredActivityId);

    when(associationService.getAllOf(any(), any(), any())).thenReturn(List.of(skillAssociation));

    when(skillAssociation.getAssociationType())
        .thenReturn(EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL);
    when(skillAssociation.getId2()).thenReturn(skillId);

    when(traceService.findAllTracesById(any())).thenReturn(List.of());

    when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of(skillId)))
        .thenReturn(List.of());

    BddLogger.when("getDeclaredActivityAssociations is called");

    BddLogger.then("DeclaredSkillProgressNotFoundException is thrown");
    assertThatThrownBy(() -> service.getDeclaredActivityAssociations(declaredActivityId))
        .isInstanceOf(DeclaredSkillProgressNotFoundException.class);
  }

  @Test
  void getDeclaredActivityAssociations_should_throw_when_declaredActivity_not_found() {
    BddLogger.given("A non-existent declared activity");

    UUID declaredActivityId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.empty());

    BddLogger.when("getDeclaredActivityAssociations is called");

    BddLogger.then("DeclaredActivityNotFoundException is thrown");
    assertThatThrownBy(() -> service.getDeclaredActivityAssociations(declaredActivityId))
        .isInstanceOf(DeclaredActivityNotFoundException.class);
  }

  @Test
  void getDeclaredActivityAssociations_should_throw_when_not_owner() {
    BddLogger.given("A declared activity belonging to another student");

    UUID declaredActivityId = UUID.randomUUID();

    Student anotherStudent = StudentFixture.create().toModel();
    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(anotherStudent);

    BddLogger.when("getDeclaredActivityAssociations is called");

    BddLogger.then("UserNotAuthorizedException is thrown");
    assertThatThrownBy(() -> service.getDeclaredActivityAssociations(declaredActivityId))
        .isInstanceOf(UserNotAuthorizedException.class);
  }

  @Test
  void associateActivityWithDeclaredSkills_should_create_associations_and_return_data_when_valid() {
    BddLogger.given(
        "A logged-in student, a declared activity owned by him and valid declared skills");

    UUID declaredActivityId = UUID.randomUUID();
    UUID skillId1 = UUID.randomUUID();
    UUID skillId2 = UUID.randomUUID();

    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

    var skill1 = mock(DeclaredSkillProgress.class);
    var skill2 = mock(DeclaredSkillProgress.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(student);

    when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(
            List.of(skillId1, skillId2)))
        .thenReturn(List.of(skill1, skill2));

    when(skill1.getId()).thenReturn(skillId1);
    when(skill2.getId()).thenReturn(skillId2);

    when(skill1.getStudent()).thenReturn(student);
    when(skill2.getStudent()).thenReturn(student);

    when(associationService.getAllOf(any(), any(), any())).thenReturn(List.of());

    BddLogger.when("associateActivityWithDeclaredSkills is called");
    service.associateActivityWithDeclaredSkills(declaredActivityId, List.of(skillId1, skillId2));

    BddLogger.then("Associations are created with correct type");
    verify(associationService)
        .createAll(
            argThat(
                list ->
                    list.size() == 2
                        && list.stream()
                            .allMatch(
                                a ->
                                    a.associationType()
                                        == EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL)));
  }

  @Test
  void associateActivityWithDeclaredSkills_should_throw_when_declaredActivity_not_found() {
    BddLogger.given("A logged-in student and a non-existent declared activity");

    UUID declaredActivityId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.empty());

    BddLogger.when("associateActivityWithDeclaredSkills is called");

    BddLogger.then("DeclaredActivityNotFoundException is thrown");
    assertThatThrownBy(
            () ->
                service.associateActivityWithDeclaredSkills(
                    declaredActivityId, List.of(UUID.randomUUID())))
        .isInstanceOf(DeclaredActivityNotFoundException.class);

    verify(associationService, never()).createAll(any());
  }

  @Test
  void associateActivityWithDeclaredSkills_should_throw_when_not_owner() {
    BddLogger.given("A logged-in student and a declared activity belonging to another student");

    UUID declaredActivityId = UUID.randomUUID();

    Student anotherStudent = StudentFixture.create().toModel();
    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(anotherStudent);

    BddLogger.when("associateActivityWithDeclaredSkills is called");

    BddLogger.then("UserNotAuthorizedException is thrown");
    assertThatThrownBy(
            () ->
                service.associateActivityWithDeclaredSkills(
                    declaredActivityId, List.of(UUID.randomUUID())))
        .isInstanceOf(UserNotAuthorizedException.class);

    verify(associationService, never()).createAll(any());
  }

  @Test
  void associateActivityWithDeclaredSkills_should_throw_when_some_skills_not_found() {
    BddLogger.given("Some declared skill ids are not found");

    UUID declaredActivityId = UUID.randomUUID();
    UUID skillId1 = UUID.randomUUID();
    UUID skillIdMissing = UUID.randomUUID();

    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
    var skill1 = mock(DeclaredSkillProgress.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(student);

    when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(
            List.of(skillId1, skillIdMissing)))
        .thenReturn(List.of(skill1));

    when(skill1.getId()).thenReturn(skillId1);

    BddLogger.when("associateActivityWithDeclaredSkills is called");

    BddLogger.then("DeclaredSkillProgressNotFoundException is thrown");
    assertThatThrownBy(
            () ->
                service.associateActivityWithDeclaredSkills(
                    declaredActivityId, List.of(skillId1, skillIdMissing)))
        .isInstanceOf(DeclaredSkillProgressNotFoundException.class);

    verify(associationService, never()).createAll(any());
  }

  @Test
  void associateActivityWithDeclaredSkills_should_throw_when_skill_not_owned_by_student() {
    BddLogger.given("A declared skill not owned by the logged-in student");

    UUID declaredActivityId = UUID.randomUUID();
    UUID skillId = UUID.randomUUID();

    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);
    var skill = mock(DeclaredSkillProgress.class);
    Student anotherStudent = StudentFixture.create().toModel();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivity.getStudent()).thenReturn(student);

    when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of(skillId)))
        .thenReturn(List.of(skill));

    when(skill.getId()).thenReturn(skillId);
    when(skill.getStudent()).thenReturn(anotherStudent);

    BddLogger.when("associateActivityWithDeclaredSkills is called");

    BddLogger.then("UserNotAuthorizedException is thrown");
    assertThatThrownBy(
            () -> service.associateActivityWithDeclaredSkills(declaredActivityId, List.of(skillId)))
        .isInstanceOf(UserNotAuthorizedException.class);

    verify(associationService, never()).createAll(any());
  }

  @Test
  void searchDeclaredActivitiesForAssociation_should_use_trace_association_when_context_is_Trace() {
    BddLogger.given("A Trace context id and class");
    UUID contextId = UUID.randomUUID();
    PageCriteria pageCriteria = new PageCriteria(0, 10);
    PageInfo pageInfo = new PageInfo(0, 10, 0);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findAllByStudent(
            eq(student), eq("kw"), eq(pageCriteria), any(FetchGraph.class)))
        .thenReturn(new PagedResult<>(List.of(), pageInfo));

    PagedResult<AssociationSearchResultData> expected = new PagedResult<>(List.of(), pageInfo);
    when(associationSearchHelper.searchForAssociation(
            eq(contextId),
            eq(Trace.class),
            eq(EAssociationType.DECLARED_ACTIVITY_TRACE),
            any(),
            any(),
            any(),
            any(),
            any(),
            any()))
        .thenReturn(expected);

    BddLogger.when("searchDeclaredActivitiesForAssociation is called with TRACE context");
    var result =
        service.searchDeclaredActivitiesForAssociation(
            contextId, EAssociationContextType.TRACE, "kw", pageCriteria);

    BddLogger.then("The helper is invoked with DECLARED_ACTIVITY_TRACE and Trace.class");
    assertThat(result).isSameAs(expected);
    verify(associationSearchHelper)
        .searchForAssociation(
            eq(contextId),
            eq(Trace.class),
            eq(EAssociationType.DECLARED_ACTIVITY_TRACE),
            any(),
            any(),
            any(),
            any(),
            any(),
            any());
  }

  @Test
  void searchDeclaredActivitiesForAssociation_should_use_skill_association_when_context_is_Skill() {
    BddLogger.given("A DeclaredSkillProgress context id and class");
    UUID contextId = UUID.randomUUID();
    PageCriteria pageCriteria = new PageCriteria(0, 10);
    PageInfo pageInfo = new PageInfo(0, 10, 0);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findAllByStudent(
            eq(student), eq("kw"), eq(pageCriteria), any(FetchGraph.class)))
        .thenReturn(new PagedResult<>(List.of(), pageInfo));

    PagedResult<AssociationSearchResultData> expected = new PagedResult<>(List.of(), pageInfo);
    when(associationSearchHelper.searchForAssociation(
            eq(contextId),
            eq(DeclaredSkillProgress.class),
            eq(EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL),
            any(),
            any(),
            any(),
            any(),
            any(),
            any()))
        .thenReturn(expected);

    BddLogger.when("searchDeclaredActivitiesForAssociation is called with DECLARED_SKILL context");
    var result =
        service.searchDeclaredActivitiesForAssociation(
            contextId, EAssociationContextType.DECLARED_SKILL, "kw", pageCriteria);

    BddLogger.then("The helper is invoked with DECLARED_ACTIVITY_DECLARED_SKILL");
    assertThat(result).isSameAs(expected);
    verify(associationSearchHelper)
        .searchForAssociation(
            eq(contextId),
            eq(DeclaredSkillProgress.class),
            eq(EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL),
            any(),
            any(),
            any(),
            any(),
            any(),
            any());
  }

  @Test
  void searchDeclaredActivitiesForAssociation_should_use_null_context_when_class_unknown() {
    BddLogger.given("A null context class");
    PageCriteria pageCriteria = new PageCriteria(0, 10);
    PageInfo pageInfo = new PageInfo(0, 10, 0);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findAllByStudent(
            eq(student), eq("kw"), eq(pageCriteria), any(FetchGraph.class)))
        .thenReturn(new PagedResult<>(List.of(), pageInfo));

    PagedResult<AssociationSearchResultData> expected = new PagedResult<>(List.of(), pageInfo);
    when(associationSearchHelper.searchForAssociation(
            eq(null), eq(null), eq(null), eq(null), any(), any(), any(), any(), any()))
        .thenReturn(expected);

    BddLogger.when("searchDeclaredActivitiesForAssociation is called with null context class");
    var result = service.searchDeclaredActivitiesForAssociation(null, null, "kw", pageCriteria);

    BddLogger.then("The helper is invoked with all association parameters null");
    assertThat(result).isSameAs(expected);
    verify(associationSearchHelper)
        .searchForAssociation(
            eq(null), eq(null), eq(null), eq(null), any(), any(), any(), any(), any());
  }

  @Test
  void updateReflection_should_throw_UserNotAuthorizedException_when_reflection_is_not_enabled() {
    BddLogger.given("A logged-in student and a declared activity where enableReflection is false");

    UUID declaredActivityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().withEnableRefection(false).toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He tries to update the reflection");

    BddLogger.then("A UserNotAuthorizedException is thrown and nothing is saved");
    assertThatThrownBy(() -> service.updateReflection(declaredActivityId, "Ma réflexion"))
        .isInstanceOf(UserNotAuthorizedException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  // ── createFeedback ────────────────────────────────────────────────────────

  @Test
  void createFeedback_should_save_feedback_with_empty_associations_when_none_linked() {
    BddLogger.given(
        "A logged-in student, his declared activity with null reflexion and no associations");
    UUID declaredActivityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(associationService.getAllOf(any(), any(), any())).thenReturn(List.of());
    when(traceService.findAllTracesById(List.of())).thenReturn(List.of());
    when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of()))
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
  void
      createFeedback_should_save_feedback_with_reflexion_and_associations_from_declared_activity() {
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

    Association traceAssociation = mock(Association.class);
    Association skillAssociation = mock(Association.class);
    Trace trace = mock(Trace.class);
    DeclaredSkillProgress skill = mock(DeclaredSkillProgress.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));

    when(traceAssociation.getAssociationType())
        .thenReturn(EAssociationType.DECLARED_ACTIVITY_TRACE);
    when(traceAssociation.getId2()).thenReturn(traceId);
    when(skillAssociation.getAssociationType())
        .thenReturn(EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL);
    when(skillAssociation.getId2()).thenReturn(skillId);

    when(associationService.getAllOf(any(), any(), any()))
        .thenReturn(List.of(traceAssociation, skillAssociation));
    when(traceService.findAllTracesById(List.of(traceId))).thenReturn(List.of(trace));
    when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of(skillId)))
        .thenReturn(List.of(skill));
    when(feedbackRepository.save(any(Feedback.class))).thenAnswer(i -> i.getArguments()[0]);

    BddLogger.when("createFeedback is called");
    service.createFeedback(declaredActivityId);

    BddLogger.then(
        "A feedback is saved with the activity's reflexion, 1 trace and 1 declared skill");
    verify(feedbackRepository).save(feedbackCaptor.capture());
    Feedback captured = feedbackCaptor.getValue();
    assertThat(captured.getReflexion().get()).isEqualTo(reflexion);
    assertThat(captured.getAssociatedTraces()).containsExactly(trace);
    assertThat(captured.getAssociatedDeclaredSkills()).containsExactly(skill);
  }

  @Test
  void createFeedback_should_throw_DeclaredActivityNotFoundException_when_activity_not_found() {
    BddLogger.given("A logged-in student and a non-existent declared activity ID");
    UUID declaredActivityId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.empty());

    BddLogger.when("createFeedback is called");

    BddLogger.then("A DeclaredActivityNotFoundException is thrown and nothing is saved");
    assertThatThrownBy(() -> service.createFeedback(declaredActivityId))
        .isInstanceOf(DeclaredActivityNotFoundException.class);

    verify(feedbackRepository, never()).save(any());
  }

  @Test
  void
      createFeedback_should_throw_UserNotAuthorizedException_when_activity_belongs_to_another_student() {
    BddLogger.given("A logged-in student and a declared activity belonging to another student");
    UUID declaredActivityId = UUID.randomUUID();
    Student anotherStudent = StudentFixture.create().toModel();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            UUID.randomUUID(), anotherStudent, activity, null, null, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("createFeedback is called");

    BddLogger.then("A UserNotAuthorizedException is thrown and nothing is saved");
    assertThatThrownBy(() -> service.createFeedback(declaredActivityId))
        .isInstanceOf(UserNotAuthorizedException.class);

    verify(feedbackRepository, never()).save(any());
  }

  @Test
  void createFeedback_should_throw_FieldValidationException_when_reflexion_exceeds_max_length() {
    BddLogger.given(
        "A logged-in student and his declared activity with a reflexion exceeding"
            + " RICH_TEXT_LENGTH");
    UUID declaredActivityId = UUID.randomUUID();
    String tooLongReflexion = "a".repeat(RICH_TEXT_LENGTH + 1);
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            UUID.randomUUID(), student, activity, null, tooLongReflexion, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("createFeedback is called");

    BddLogger.then("A FieldValidationException is thrown and nothing is saved");
    assertThatThrownBy(() -> service.createFeedback(declaredActivityId))
        .isInstanceOf(FieldValidationException.class);

    verify(feedbackRepository, never()).save(any());
  }

  @Test
  void createFeedback_should_throw_FeedbackInProcessException_when_last_feedback_is_IN_PROCESS() {
    BddLogger.given(
        "A logged-in student whose last feedback on the declared activity is IN_PROCESS");
    UUID declaredActivityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    Feedback inProcessFeedback =
        Feedback.toDomain(
            UUID.randomUUID(),
            Instant.now(),
            Instant.now(),
            declaredActivity,
            null,
            null,
            EFeedbackStatus.IN_PROCESS,
            List.of(),
            List.of());

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(feedbackRepository.findAllByDeclaredActivityId(declaredActivityId))
        .thenReturn(List.of(inProcessFeedback));

    BddLogger.when("createFeedback is called");

    BddLogger.then("A FeedbackInProcessException is thrown and nothing is saved");
    assertThatThrownBy(() -> service.createFeedback(declaredActivityId))
        .isInstanceOf(FeedbackInProcessException.class);

    verify(feedbackRepository, never()).save(any());
  }

  @Test
  void createFeedback_should_update_existing_feedback_when_last_feedback_is_NEW() {
    BddLogger.given(
        "A logged-in student whose last feedback is NEW, and the declared activity has a reflexion"
            + " and associations");
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
            List.of(),
            List.of());

    Association traceAssociation = mock(Association.class);
    Association skillAssociation = mock(Association.class);
    Trace trace = mock(Trace.class);
    DeclaredSkillProgress skill = mock(DeclaredSkillProgress.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(feedbackRepository.findAllByDeclaredActivityId(declaredActivityId))
        .thenReturn(List.of(existingFeedback));

    when(traceAssociation.getAssociationType())
        .thenReturn(EAssociationType.DECLARED_ACTIVITY_TRACE);
    when(traceAssociation.getId2()).thenReturn(traceId);
    when(skillAssociation.getAssociationType())
        .thenReturn(EAssociationType.DECLARED_ACTIVITY_DECLARED_SKILL);
    when(skillAssociation.getId2()).thenReturn(skillId);
    when(associationService.getAllOf(any(), any(), any()))
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
  void
      createFeedback_should_create_new_feedback_when_last_feedback_is_SUBMITTED_and_limit_not_reached() {
    BddLogger.given(
        "A logged-in student with 1 SUBMITTED feedback and feedbackAllowedIterations=2");
    UUID declaredActivityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().withFeedbackAllowedIterations(2).toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    Feedback submittedFeedback =
        Feedback.toDomain(
            UUID.randomUUID(),
            Instant.now(),
            Instant.now(),
            declaredActivity,
            null,
            "Retour du formateur",
            EFeedbackStatus.SUBMITTED,
            List.of(),
            List.of());

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(feedbackRepository.findAllByDeclaredActivityId(declaredActivityId))
        .thenReturn(List.of(submittedFeedback));
    when(associationService.getAllOf(any(), any(), any())).thenReturn(List.of());
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
  void
      createFeedback_should_throw_FeedbackMaximumIterationReachedException_when_limit_is_reached() {
    BddLogger.given(
        "A logged-in student with 2 SUBMITTED feedbacks and feedbackAllowedIterations=2");
    UUID declaredActivityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().withFeedbackAllowedIterations(2).toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    Feedback feedback1 =
        Feedback.toDomain(
            UUID.randomUUID(),
            Instant.now().minusSeconds(120),
            Instant.now().minusSeconds(120),
            declaredActivity,
            null,
            "Retour 1",
            EFeedbackStatus.SUBMITTED,
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
            List.of(),
            List.of());

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(feedbackRepository.findAllByDeclaredActivityId(declaredActivityId))
        .thenReturn(List.of(feedback1, feedback2));

    BddLogger.when("createFeedback is called");

    BddLogger.then("A FeedbackMaximumIterationReachedException is thrown and nothing is saved");
    assertThatThrownBy(() -> service.createFeedback(declaredActivityId))
        .isInstanceOf(FeedbackMaximumIterationReachedException.class);

    verify(feedbackRepository, never()).save(any());
  }

  @Test
  void
      associateActivityWithTraces_should_throw_MaximumAssociationReachedException_when_limit_reached() {
    BddLogger.given(
        "A declared activity with traceAllowedAssociations=1 and already 1 existing association");

    UUID declaredActivityId = UUID.randomUUID();
    UUID traceId = UUID.randomUUID();

    Activity activity = ActivityFixture.create().withTraceAllowedAssociations(1).toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    Trace trace = mock(Trace.class);
    when(trace.getId()).thenReturn(traceId);
    when(trace.getUser()).thenReturn(student.getUser());

    Association existingAssociation = mock(Association.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(eq(declaredActivityId), any(FetchGraph.class)))
        .thenReturn(Optional.of(declaredActivity));
    when(traceService.findAllTracesById(List.of(traceId))).thenReturn(List.of(trace));
    when(associationService.getAllOf(
            declaredActivityId,
            DeclaredActivity.class,
            List.of(EAssociationType.DECLARED_ACTIVITY_TRACE)))
        .thenReturn(List.of(existingAssociation));

    BddLogger.when("He tries to associate a new trace");

    BddLogger.then("A MaximumAssociationReachedException is thrown and no association is created");
    assertThatThrownBy(
            () -> service.associateActivityWithTraces(declaredActivityId, List.of(traceId)))
        .isInstanceOf(MaximumAssociationReachedException.class);

    verify(associationService, never()).createAll(any());
  }
}
