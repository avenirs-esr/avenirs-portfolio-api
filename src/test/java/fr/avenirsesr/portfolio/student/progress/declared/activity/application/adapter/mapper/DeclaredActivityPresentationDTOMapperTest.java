package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.activity.application.adapter.mapper.ActivityContentDtoMapper;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityDetailsData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeclaredActivityPresentationDTOMapperTest {

  @Spy
  private ActivityContentDtoMapper activityContentDtoMapper =
      Mappers.getMapper(ActivityContentDtoMapper.class);

  @Spy
  private FeedbackOverviewDTOMapper feedbackOverviewDTOMapper =
      Mappers.getMapper(FeedbackOverviewDTOMapper.class);

  @InjectMocks private DeclaredActivityDetailsDTOMapperImpl mapper;

  @Test
  void shouldMapDeclaredActivityToDetailsDTO() {
    BddLogger.given("a declared activity");
    Student student = StudentFixture.create().toModel();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(
            UUID.randomUUID(),
            student,
            activity,
            null,
            "My reflection",
            LocalDate.now(),
            LocalDate.now().plusMonths(3),
            null);

    BddLogger.when("mapping to DeclaredActivityDetailsDTO");
    DeclaredActivityDetailsDTO dto =
        mapper.toDTO(new DeclaredActivityDetailsData(declaredActivity, List.of()));

    BddLogger.then("it should map activity via ActivityDtoMapper and include status");
    assertNotNull(dto);
    assertEquals(declaredActivity.getId(), dto.id());
    assertNotNull(dto.activity());
    assertEquals(activity.getId(), dto.activity().id());
    assertEquals(activity.getTitle(), dto.activity().title());
    assertNotNull(dto.status());
  }

  @Test
  void shouldMapFeedbacksFromDetailsDataToFeedbackOverviewDTOs() {
    BddLogger.given("a declared activity with 2 feedbacks");
    Student student = StudentFixture.create().toModel();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    Feedback feedback1 =
        Feedback.toDomain(
            UUID.randomUUID(),
            Instant.now(),
            Instant.now(),
            declaredActivity,
            null,
            null,
            EFeedbackStatus.NEW,
            List.of(),
            List.of());
    Feedback feedback2 =
        Feedback.toDomain(
            UUID.randomUUID(),
            Instant.now(),
            Instant.now(),
            declaredActivity,
            null,
            "Mon retour",
            EFeedbackStatus.SUBMITTED,
            List.of(),
            List.of());

    BddLogger.when("mapping DeclaredActivityDetailsData with feedbacks");
    DeclaredActivityDetailsDTO dto =
        mapper.toDTO(
            new DeclaredActivityDetailsData(declaredActivity, List.of(feedback1, feedback2)));

    BddLogger.then("the feedbacks list is mapped and contains 2 FeedbackOverviewDTOs");
    assertNotNull(dto.feedbacks());
    assertEquals(2, dto.feedbacks().size());
    assertEquals(feedback1.getId(), dto.feedbacks().get(0).id());
    assertEquals(EFeedbackStatus.NEW, dto.feedbacks().get(0).status());
    assertEquals(feedback2.getId(), dto.feedbacks().get(1).id());
    assertEquals(EFeedbackStatus.SUBMITTED, dto.feedbacks().get(1).status());
  }

  @Test
  void shouldMapEmptyFeedbacksListWhenNoFeedbacks() {
    BddLogger.given("a declared activity with no feedbacks");
    Student student = StudentFixture.create().toModel();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    BddLogger.when("mapping DeclaredActivityDetailsData with empty feedbacks");
    DeclaredActivityDetailsDTO dto =
        mapper.toDTO(new DeclaredActivityDetailsData(declaredActivity, List.of()));

    BddLogger.then("the feedbacks list is empty");
    assertNotNull(dto.feedbacks());
    assertTrue(dto.feedbacks().isEmpty());
  }
}
