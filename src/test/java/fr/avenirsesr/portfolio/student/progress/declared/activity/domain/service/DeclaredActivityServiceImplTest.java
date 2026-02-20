package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.activity.domain.exception.ActivityNotFoundException;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityAlreadyExistException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityAlreadyFinishedException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityHasNotStartedException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.Instant;
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
  @Mock private LoggedInUserService loggedInUserService;
  private DeclaredActivityService declaredActivityService;

  @InjectMocks private DeclaredActivityServiceImpl service;

  @Captor private ArgumentCaptor<DeclaredActivity> activityCaptor;

  private Student student;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
    declaredActivityService =
        new DeclaredActivityServiceImpl(
            declaredActivityRepository, activityRepository, loggedInUserService);
  }

  @Test
  void subscribe_should_create_and_save_when_activity_exists_and_not_already_subscribed() {
    BddLogger.given("A logged-in student and an existing activity he is not subscribed to");
    UUID activityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.isSubscribedTo(student, activity)).thenReturn(false);
    when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));

    BddLogger.when("He try to subscribe to this activity");
    service.subscribe(activityId);

    BddLogger.then("A new DeclaredActivity is created and saved");
    verify(declaredActivityRepository).save(activityCaptor.capture());
    DeclaredActivity savedActivity = activityCaptor.getValue();

    assertThat(savedActivity.getStudent()).isEqualTo(student);
    assertThat(savedActivity.getActivity()).isEqualTo(activity);
    assertThat(savedActivity.isHasStarted()).isFalse();
  }

  @Test
  void subscribe_should_throw_ActivityNotFoundException_when_activity_does_not_exist() {
    BddLogger.given("A logged-in student and a non-existent activity ID");
    UUID activityId = UUID.randomUUID();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

    BddLogger.when("He try to subscribe to this activity");

    BddLogger.then("An ActivityNotFoundException is thrown and nothing is saved");
    assertThatThrownBy(() -> service.subscribe(activityId))
        .isInstanceOf(ActivityNotFoundException.class);

    verify(declaredActivityRepository, never()).isSubscribedTo(any(), any());
    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void subscribe_should_throw_DeclaredActivityAlreadyExistException_when_already_subscribed() {
    BddLogger.given("A logged-in student already subscribed to an existing activity");
    Activity activity = ActivityFixture.create().toModel();

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(activityRepository.findById(activity.getId())).thenReturn(Optional.of(activity));
    when(declaredActivityRepository.isSubscribedTo(student, activity)).thenReturn(true);

    BddLogger.when("He try to subscribe to it again");

    BddLogger.then("A DeclaredActivityAlreadyExistException is thrown");
    assertThatThrownBy(() -> service.subscribe(activity.getId()))
        .isInstanceOf(DeclaredActivityAlreadyExistException.class);

    verify(declaredActivityRepository, never()).save(any());
  }

  @Test
  void shouldRemoveDeclaredActivities_whenOwnedByStudent() {
    var student = mock(Student.class);
    var declaredActivity1 = mock(DeclaredActivity.class);
    var declaredActivity2 = mock(DeclaredActivity.class);
    var idDeclaredActivity1 = UUID.randomUUID();
    var idDeclaredActivity2 = UUID.randomUUID();
    var ids = List.of(idDeclaredActivity1, idDeclaredActivity2);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findAllById(anyList()))
        .thenReturn(List.of(declaredActivity1, declaredActivity2));

    when(declaredActivity1.getStudent()).thenReturn(student);
    when(declaredActivity2.getStudent()).thenReturn(student);

    when(declaredActivity1.getId()).thenReturn(idDeclaredActivity1);
    when(declaredActivity2.getId()).thenReturn(idDeclaredActivity2);

    declaredActivityService.unsubscribeMultiple(ids);

    verify(declaredActivityRepository)
        .removeAllFromDatabase(List.of(declaredActivity1, declaredActivity2));
  }

  @Test
  void shouldThrowException_whenDeclaredActivitiesNotFound() {
    var ids = List.of(UUID.randomUUID());
    var student = mock(Student.class);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findAllById(ids)).thenReturn(List.of());

    Assertions.assertThrows(
        DeclaredActivityNotFoundException.class,
        () -> declaredActivityService.unsubscribeMultiple(ids));

    verify(declaredActivityRepository, never()).removeAllFromDatabase(any());
  }

  @Test
  void finish_should_update_finishedAt_and_save_when_valid() {
    BddLogger.given("A logged-in student and his existing started declared activity");
    UUID declaredActivityId = UUID.randomUUID();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(student, activity, true, null, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));
    when(declaredActivityRepository.save(any(DeclaredActivity.class)))
        .thenAnswer(i -> i.getArguments()[0]);

    BddLogger.when("He try to finish this activity");
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
        DeclaredActivity.create(student, activity, false, null, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He try to finish this activity");

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

    BddLogger.when("He try to finish this activity");

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
        DeclaredActivity.create(anotherStudent, activity, false, null, null, null, null);

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He try to finish this activity");

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
        DeclaredActivity.create(student, activity, false, null, null, null, null);

    declaredActivity.setHasStarted(true);
    declaredActivity.setFinishedAt(Instant.now());

    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityRepository.findById(declaredActivityId))
        .thenReturn(Optional.of(declaredActivity));

    BddLogger.when("He try to finish this activity again");

    BddLogger.then("A DeclaredActivityAlreadyFinishedException is thrown");
    assertThatThrownBy(() -> service.finish(declaredActivityId))
        .isInstanceOf(DeclaredActivityAlreadyFinishedException.class);

    verify(declaredActivityRepository, never()).save(any());
  }
}
