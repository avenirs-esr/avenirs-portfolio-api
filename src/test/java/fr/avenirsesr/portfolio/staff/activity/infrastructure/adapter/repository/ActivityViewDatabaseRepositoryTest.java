package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityViewEntity;
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
    when(activityViewJpaRepository.existsByActivityIdAndStudentId(activityId, studentId))
        .thenReturn(false);

    BddLogger.when("The consultation is recorded");
    repository.recordView(activityId, studentId);

    BddLogger.then("A view is saved for this student and this activity");
    verify(activityViewJpaRepository).saveAndFlush(viewCaptor.capture());
    assertThat(viewCaptor.getValue().getActivityId()).isEqualTo(activityId);
    assertThat(viewCaptor.getValue().getStudentId()).isEqualTo(studentId);
  }

  @Test
  void recordView_should_not_save_a_second_view_for_the_same_student() {
    BddLogger.given("A student who already consulted the activity");
    when(activityViewJpaRepository.existsByActivityIdAndStudentId(activityId, studentId))
        .thenReturn(true);

    BddLogger.when("The consultation is recorded again");
    repository.recordView(activityId, studentId);

    BddLogger.then("No additional view is saved");
    verify(activityViewJpaRepository, never()).saveAndFlush(any());
  }

  @Test
  void recordView_should_ignore_a_view_recorded_concurrently_by_the_same_student() {
    BddLogger.given("A student whose first consultation is recorded twice concurrently");
    when(activityViewJpaRepository.existsByActivityIdAndStudentId(activityId, studentId))
        .thenReturn(false);
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
}
