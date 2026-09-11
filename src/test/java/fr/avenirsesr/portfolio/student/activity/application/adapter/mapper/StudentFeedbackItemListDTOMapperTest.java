package fr.avenirsesr.portfolio.student.activity.application.adapter.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.StudentFeedbackItemListDTO;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.user.application.adapter.dto.StudentInfoDTO;
import fr.avenirsesr.portfolio.user.application.adapter.mapper.StudentInfoDTOMapper;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StudentFeedbackItemListDTOMapperTest {

  @Mock private StudentInfoDTOMapper studentInfoDTOMapper;

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
        List.of(),
        List.of());
  }

  private StudentInfoDTO studentInfoOf(Student student) {
    return new StudentInfoDTO(
        student.getUser().getId(),
        student.getUser().getFirstName(),
        student.getUser().getLastName(),
        student.getUser().getEmail(),
        null);
  }

  @Test
  void should_map_id_and_student_from_feedback() {
    BddLogger.given("A feedback linked to a declared activity with a student");
    Student student = StudentFixture.create().toModel();
    Feedback feedback = buildFeedback(student);
    when(studentInfoDTOMapper.toDTO(student)).thenReturn(studentInfoOf(student));

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
    when(studentInfoDTOMapper.toDTO(student1)).thenReturn(studentInfoOf(student1));
    when(studentInfoDTOMapper.toDTO(student2)).thenReturn(studentInfoOf(student2));

    BddLogger.when("mapping both feedbacks independently");
    StudentFeedbackItemListDTO dto1 = mapper.toDTO(feedback1);
    StudentFeedbackItemListDTO dto2 = mapper.toDTO(feedback2);

    BddLogger.then("each dto carries the correct student identity");
    assertThat(dto1.student().id()).isEqualTo(student1.getUser().getId());
    assertThat(dto2.student().id()).isEqualTo(student2.getUser().getId());
    assertThat(dto1.feedbackId()).isNotEqualTo(dto2.feedbackId());
  }

  @Test
  void should_return_null_when_feedback_is_null() {
    BddLogger.given("No feedback");
    BddLogger.when("mapping a null feedback");
    StudentFeedbackItemListDTO dto = mapper.toDTO(null);

    BddLogger.then("the result is null and the student info mapper is never called");
    assertThat(dto).isNull();
    verify(studentInfoDTOMapper, never()).toDTO(any());
  }
}
