package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityViewEntity;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class ActivityViewDatabaseRepositoryTest {

  @Mock private ActivityViewJpaRepository activityViewJpaRepository;

  @Captor private ArgumentCaptor<ActivityViewEntity> viewCaptor;

  private ActivityViewDatabaseRepository repository;

  private UUID activityId;
  private UUID studentId;

  @BeforeEach
  void setUp() {
    repository = new ActivityViewDatabaseRepository(activityViewJpaRepository);
    activityId = UUID.randomUUID();
    studentId = UUID.randomUUID();
  }

  @Test
  void recordView_should_save_the_view_when_the_student_never_consulted_the_activity() {
    BddLogger.given("A student who never consulted the activity");
    when(activityViewJpaRepository.updateLastViewedAt(eq(activityId), eq(studentId), any()))
        .thenReturn(0);

    BddLogger.when("The consultation is recorded");
    var beforeCall = Instant.now();
    repository.recordView(activityId, studentId);

    BddLogger.then("A view is saved for this student and this activity");
    verify(activityViewJpaRepository).saveAndFlush(viewCaptor.capture());
    assertThat(viewCaptor.getValue().getActivityId()).isEqualTo(activityId);
    assertThat(viewCaptor.getValue().getStudentId()).isEqualTo(studentId);
    assertThat(viewCaptor.getValue().getLastViewedAt()).isAfterOrEqualTo(beforeCall);
  }

  @Test
  void recordView_should_not_save_a_second_view_for_the_same_student() {
    BddLogger.given("A student who already consulted the activity");
    when(activityViewJpaRepository.updateLastViewedAt(eq(activityId), eq(studentId), any()))
        .thenReturn(1);

    BddLogger.when("The consultation is recorded again");
    repository.recordView(activityId, studentId);

    BddLogger.then("No additional view is saved");
    verify(activityViewJpaRepository, never()).saveAndFlush(any());
  }

  @Test
  void recordView_should_refresh_the_last_consultation_date_of_an_existing_view() {
    BddLogger.given("A student who already consulted the activity");
    when(activityViewJpaRepository.updateLastViewedAt(eq(activityId), eq(studentId), any()))
        .thenReturn(1);

    BddLogger.when("The consultation is recorded again");
    var beforeCall = Instant.now();
    repository.recordView(activityId, studentId);

    BddLogger.then("The last consultation date of the view is refreshed");
    var viewedAtCaptor = ArgumentCaptor.forClass(Instant.class);
    verify(activityViewJpaRepository)
        .updateLastViewedAt(eq(activityId), eq(studentId), viewedAtCaptor.capture());
    assertThat(viewedAtCaptor.getValue()).isAfterOrEqualTo(beforeCall);
  }

  @Test
  void recordView_should_ignore_a_view_recorded_concurrently_by_the_same_student() {
    BddLogger.given("A student whose first consultation is recorded twice concurrently");
    when(activityViewJpaRepository.updateLastViewedAt(eq(activityId), eq(studentId), any()))
        .thenReturn(0);
    when(activityViewJpaRepository.saveAndFlush(any()))
        .thenThrow(new DataIntegrityViolationException("uk_activity_view_activity_student"));

    BddLogger.when("The consultation is recorded");

    BddLogger.then("The unique constraint violation is swallowed");
    assertThatCode(() -> repository.recordView(activityId, studentId)).doesNotThrowAnyException();
  }

  @Test
  void countUniqueViews_should_delegate_to_the_jpa_repository() {
    BddLogger.given("An activity consulted by 12 distinct students");
    when(activityViewJpaRepository.countByActivityId(activityId)).thenReturn(12);

    BddLogger.when("The unique views are counted");
    var count = repository.countUniqueViews(activityId);

    BddLogger.then("The count of the repository is returned");
    assertThat(count).isEqualTo(12);
  }

  @Test
  void countViewersSince_should_delegate_to_the_jpa_repository() {
    BddLogger.given("Two students having consulted the activity since a given date");
    var studentIds = List.of(studentId, UUID.randomUUID(), UUID.randomUUID());
    var since = Instant.now().minus(Duration.ofDays(30));
    when(activityViewJpaRepository.countByActivityIdAndStudentIdInAndLastViewedAtGreaterThanEqual(
            activityId, studentIds, since))
        .thenReturn(2);

    BddLogger.when("The recent viewers are counted");
    var count = repository.countViewersSince(activityId, studentIds, since);

    BddLogger.then("The count of the repository is returned");
    assertThat(count).isEqualTo(2);
  }

  @Test
  void countViewersSince_should_not_query_the_jpa_repository_without_student() {
    BddLogger.given("No student to look for");

    BddLogger.when("The recent viewers are counted");
    var count = repository.countViewersSince(activityId, List.of(), Instant.now());

    BddLogger.then("No count is requested and zero is returned");
    assertThat(count).isZero();
    verify(activityViewJpaRepository, never())
        .countByActivityIdAndStudentIdInAndLastViewedAtGreaterThanEqual(any(), any(), any());
  }
}
