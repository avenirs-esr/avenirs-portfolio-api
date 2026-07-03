package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityContentDTO;
import fr.avenirsesr.portfolio.activity.application.adapter.mapper.ActivityContentDtoMapper;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityDetailsData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.FeedbackData;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
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
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeclaredActivityPresentationDTOMapperTest {

  @Mock private ActivityContentDtoMapper activityContentDtoMapper;

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

    when(activityContentDtoMapper.toDTO(activity)).thenReturn(toActivityContentDTO(activity));

    BddLogger.when("mapping to DeclaredActivityDetailsDTO");

    DeclaredActivityDetailsDTO dto =
        mapper.toDTO(
            new DeclaredActivityDetailsData(declaredActivity, List.of()),
            EDeclaredActivityStatus.SUBSCRIBED);

    BddLogger.then("it should map activity via ActivityDtoMapper and include status");

    assertNotNull(dto);
    assertEquals(declaredActivity.getId(), dto.id());
    assertNotNull(dto.activity());
    assertEquals(activity.getId(), dto.activity().id());
    assertEquals(activity.getTitle(), dto.activity().title());
    assertEquals(EDeclaredActivityStatus.SUBSCRIBED, dto.status());
  }

  @Test
  void shouldMapFeedbacksFromDetailsDataToFeedbackOverviewDTOs() {
    BddLogger.given("a declared activity with 2 FeedbackData");

    Student student = StudentFixture.create().toModel();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    when(activityContentDtoMapper.toDTO(activity)).thenReturn(toActivityContentDTO(activity));

    UUID feedbackId1 = UUID.randomUUID();
    UUID feedbackId2 = UUID.randomUUID();

    FeedbackData feedbackData1 =
        new FeedbackData(
            feedbackId1,
            Instant.now(),
            Instant.now(),
            declaredActivity,
            null,
            null,
            EFeedbackStatus.NEW,
            1,
            List.of(),
            List.of());

    FeedbackData feedbackData2 =
        new FeedbackData(
            feedbackId2,
            Instant.now(),
            Instant.now(),
            declaredActivity,
            null,
            "Mon retour",
            EFeedbackStatus.SUBMITTED,
            2,
            List.of(),
            List.of());

    BddLogger.when("mapping DeclaredActivityDetailsData with feedbacks");

    DeclaredActivityDetailsDTO dto =
        mapper.toDTO(
            new DeclaredActivityDetailsData(
                declaredActivity, List.of(feedbackData1, feedbackData2)),
            EDeclaredActivityStatus.SUBMITTED);

    BddLogger.then("the feedbacks list is mapped and contains 2 FeedbackOverviewDTOs");

    assertNotNull(dto.feedbacks());
    assertEquals(2, dto.feedbacks().size());
    assertEquals(EDeclaredActivityStatus.SUBMITTED, dto.status());
    assertEquals(feedbackId1, dto.feedbacks().get(0).id());
    assertEquals(EFeedbackStatus.NEW, dto.feedbacks().get(0).status());
    assertEquals(feedbackId2, dto.feedbacks().get(1).id());
    assertEquals(EFeedbackStatus.SUBMITTED, dto.feedbacks().get(1).status());
  }

  @Test
  void shouldMapEmptyFeedbacksListWhenNoFeedbacks() {
    BddLogger.given("a declared activity with no feedbacks");

    Student student = StudentFixture.create().toModel();
    Activity activity = ActivityFixture.create().toModel();
    DeclaredActivity declaredActivity =
        DeclaredActivity.create(UUID.randomUUID(), student, activity, null, null, null, null, null);

    when(activityContentDtoMapper.toDTO(activity)).thenReturn(toActivityContentDTO(activity));

    BddLogger.when("mapping DeclaredActivityDetailsData with empty feedbacks");

    DeclaredActivityDetailsDTO dto =
        mapper.toDTO(
            new DeclaredActivityDetailsData(declaredActivity, List.of()),
            EDeclaredActivityStatus.SUBSCRIBED);

    BddLogger.then("the feedbacks list is empty");

    assertNotNull(dto.feedbacks());
    assertTrue(dto.feedbacks().isEmpty());
    assertEquals(EDeclaredActivityStatus.SUBSCRIBED, dto.status());
  }

  private ActivityContentDTO toActivityContentDTO(Activity activity) {
    return new ActivityContentDTO(
        activity.getId(),
        activity.getTitle(),
        activity.getThematic(),
        activity.getSummary(),
        activity.getDescription().orElse(null),
        activity.getExecutionPeriodInfo().orElse(null),
        activity.isEnableReflection(),
        activity.getTraceAllowedAssociations(),
        activity.getFeedbackAllowedIterations(),
        true,
        List.of(),
        List.of(),
        activity.getCreatedAt(),
        activity.getUpdatedAt());
  }
}
