package fr.avenirsesr.portfolio.student.activity.application.adapter.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.StudentFeedbackItemListDTO;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentFeedbackItemListDTOMapperTest {

  @InjectMocks private StudentFeedbackItemListDTOMapperImpl mapper;

  private Feedback buildFeedback(Student student) {
    var activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);
    return Feedback.toDomain(
        UUID.randomUUID(),
        Instant.now(),
        Instant.now(),
        declaredActivity,
        null,
        null,
        EFeedbackStatus.NEW,
        1,
        List.of(),
        List.of());
  }

  @Test
  void should_map_id_and_student_from_feedback() {
    BddLogger.given("A feedback linked to a declared activity with a student");
    Student student = StudentFixture.create().toModel();
    Feedback feedback = buildFeedback(student);

    BddLogger.when("mapping to StudentFeedbackItemListDTO");
    StudentFeedbackItemListDTO dto = mapper.toDTO(feedback);

    BddLogger.then("id and all student fields are correctly mapped");
    assertThat(dto).isNotNull();
    assertThat(dto.feedbackId()).isEqualTo(feedback.getId());
    assertThat(dto.student()).isNotNull();
    assertThat(dto.student().id()).isEqualTo(student.getUser().getId());
    assertThat(dto.student().firstName()).isEqualTo(student.getUser().getFirstName());
    assertThat(dto.student().lastName()).isEqualTo(student.getUser().getLastName());
    assertThat(dto.student().email()).isEqualTo(student.getUser().getEmail());
  }

  @Test
  void should_map_student_independently_per_feedback() {
    BddLogger.given("Two feedbacks belonging to two different students");
    Student student1 = StudentFixture.create().toModel();
    Student student2 = StudentFixture.create().toModel();
    Feedback feedback1 = buildFeedback(student1);
    Feedback feedback2 = buildFeedback(student2);

    BddLogger.when("mapping both feedbacks independently");
    StudentFeedbackItemListDTO dto1 = mapper.toDTO(feedback1);
    StudentFeedbackItemListDTO dto2 = mapper.toDTO(feedback2);

    BddLogger.then("each dto carries the correct student identity");
    assertThat(dto1.student().id()).isEqualTo(student1.getUser().getId());
    assertThat(dto2.student().id()).isEqualTo(student2.getUser().getId());
    assertThat(dto1.feedbackId()).isNotEqualTo(dto2.feedbackId());
  }
}
