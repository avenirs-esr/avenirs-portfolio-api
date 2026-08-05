package fr.avenirsesr.portfolio.student.activity.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.activity.application.adapter.dto.FeedbackOverviewDTO;
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
class FeedbackOverviewDTOMapperTest {

  @InjectMocks private FeedbackOverviewDTOMapperImpl mapper;

  @Test
  void shouldMapFeedbackToOverviewDTO_withStudentAndStaffFromActivity() {
    BddLogger.given(
        "A feedback linked to a declared activity with a student and a staff as activity author");
    Student student = StudentFixture.create().toModel();
    var activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);
    Feedback feedback =
        Feedback.toDomain(
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

    BddLogger.when("mapping to FeedbackOverviewDTO");
    FeedbackOverviewDTO dto = mapper.toDTO(feedback);

    BddLogger.then("id, status, student and staff are correctly mapped");
    assertNotNull(dto);
    assertEquals(feedback.getId(), dto.id());
    assertEquals(EFeedbackStatus.NEW, dto.status());
    assertNotNull(dto.student());
    assertEquals(student.getUser().getId(), dto.student().id());
    assertEquals(student.getUser().getFirstName(), dto.student().firstName());
    assertEquals(student.getUser().getLastName(), dto.student().lastName());
    assertNotNull(dto.staff());
    assertEquals(activity.getAuthor().getUser().getId(), dto.staff().id());
    assertNull(dto.feedback());
    assertNotNull(dto.createdAt());
    assertNotNull(dto.updatedAt());
  }

  @Test
  void shouldMapFeedbackTextToDTO_whenFeedbackPresent() {
    BddLogger.given("A feedback with a non-null feedback text and SUBMITTED status");
    Student student = StudentFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            UUID.randomUUID(),
            student,
            ActivityFixture.create().toModel(),
            null,
            null,
            null,
            null,
            null);
    Feedback feedback =
        Feedback.toDomain(
            UUID.randomUUID(),
            Instant.now(),
            Instant.now(),
            declaredActivity,
            null,
            "Voici mon retour détaillé",
            EFeedbackStatus.SUBMITTED,
            1,
            List.of(),
            List.of());

    BddLogger.when("mapping to FeedbackOverviewDTO");
    FeedbackOverviewDTO dto = mapper.toDTO(feedback);

    BddLogger.then("the feedback text and status are correctly mapped");
    assertEquals("Voici mon retour détaillé", dto.feedback());
    assertEquals(EFeedbackStatus.SUBMITTED, dto.status());
  }

  @Test
  void shouldMapFeedbackToOverviewDTO_withNullFeedbackWhenStatusIsNew() {
    BddLogger.given("A newly created feedback with no feedback text yet");
    Student student = StudentFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            UUID.randomUUID(),
            student,
            ActivityFixture.create().toModel(),
            null,
            null,
            null,
            null,
            null);
    Feedback feedback =
        Feedback.toDomain(
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

    BddLogger.when("mapping to FeedbackOverviewDTO");
    FeedbackOverviewDTO dto = mapper.toDTO(feedback);

    BddLogger.then("feedback text is null and status is NEW");
    assertNull(dto.feedback());
    assertEquals(EFeedbackStatus.NEW, dto.status());
    assertEquals(feedback.getId(), dto.id());
  }
}
