package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.activity.domain.exception.ActivityNotFoundException;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.association.domain.model.Association;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.common.data.domain.FetchGraph;
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
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
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
  @Mock private ActivityRepository activityRepository;
  @Mock private TraceRepository traceRepository;
  @Mock private AssociationService associationService;
  @Mock private LoggedInUserService loggedInUserService;

  @InjectMocks private DeclaredActivityServiceImpl service;

  private DeclaredActivityService declaredActivityService;

  @Captor private ArgumentCaptor<DeclaredActivity> activityCaptor;

  private Student student;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
    declaredActivityService =
        new DeclaredActivityServiceImpl(
            declaredActivityRepository,
            activityRepository,
            traceRepository,
            associationService,
            loggedInUserService);
  }

  @Test
  void subscribe_should_create_and_save_when_activity_exists_and_not_already_subscribed() {
    BddLogger.given("A logged-in student and an existing activity he is not subscribed to");
    UUID activityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findByActivity(student, activity)).thenReturn(Optional.empty());
    when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));

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
    when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

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
    when(activityRepository.findById(activity.getId())).thenReturn(Optional.of(activity));
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
    when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));

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
    when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));

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
    when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));

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

    BddLogger.when("He requests to unsubscribe from these activities");
    declaredActivityService.unsubscribeMultiple(activityIds);

    BddLogger.then("The student is removed from all activities.");
    verify(declaredActivityRepository)
        .removeAllFromDatabase(List.of(declaredActivity1, declaredActivity2));
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
    Student student = mock(Student.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivity.getStudent()).thenReturn(student);

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
}
