package fr.avenirsesr.portfolio.student.progress.declared.activity.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.activity.domain.exception.ActivityNotFoundException;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityAlreadyExistException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.output.repository.DeclaredActivityRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.Optional;
import java.util.UUID;
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

  @InjectMocks private DeclaredActivityServiceImpl service;

  @Captor private ArgumentCaptor<DeclaredActivity> activityCaptor;

  private Student student;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
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
}
