package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.data.FileData;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityPresentationDTO;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.SubActivityOverviewDTO;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.mapper.ActivityPresentationDtoMapper;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityPresentationData;
import fr.avenirsesr.portfolio.staff.activity.domain.data.SubActivityOverviewData;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ActivityPresentationDtoMapperTest {

  private final ActivityPresentationDtoMapper mapper =
      Mappers.getMapper(ActivityPresentationDtoMapper.class);

  @Test
  void shouldMapActivityDetailDataToDTO() {
    BddLogger.given("an activity detail data with a banner and no declared activity");
    UUID id = UUID.randomUUID();
    UUID bannerId = UUID.randomUUID();
    String baseUrl = "https://cdn.example.com/";
    var activity =
        ActivityFixture.create()
            .withId(id)
            .withTitle("Activity Title")
            .withThematic(EActivityThematic.EXPERIENCES)
            .toModel();
    var banner =
        new FileData(Optional.of(bannerId), Optional.of("banner.png"), "banners/banner.png");
    ActivityPresentationData data =
        new ActivityPresentationData(
            activity.getId(),
            activity.getTitle(),
            activity.getThematic(),
            activity.getStatus(),
            Optional.empty(),
            Optional.empty(),
            activity.getSummary(),
            activity.getDescription(),
            activity.getRecommendedCompletionContexts().orElse(null),
            banner,
            Optional.empty(),
            List.of(),
            activity.getCreatedAt(),
            activity.getUpdatedAt());

    BddLogger.when("mapping to ActivityDetailsDTO with base URL");
    ActivityPresentationDTO dto = mapper.toDTO(data, baseUrl);

    BddLogger.then("it should build the full banner URL and map all fields");
    assertNotNull(dto);
    assertEquals(id, dto.id());
    assertEquals("Activity Title", dto.title());
    assertEquals(EActivityThematic.EXPERIENCES, dto.thematic());
    assertEquals(activity.getStatus(), dto.activityStatus());
    assertNull(dto.subscribedDeclaredActivity());
    assertNull(dto.subscribedDeclaredActivityStatus());
    assertNotNull(dto.banner());
    assertEquals(bannerId, dto.banner().id());
    assertEquals("banner.png", dto.banner().fileName());
    assertEquals(baseUrl + "banners/banner.png", dto.banner().url());
    assertEquals(activity.getCreatedAt(), dto.createdAt());
    assertEquals(activity.getUpdatedAt(), dto.updatedAt());
  }

  @Test
  void shouldMapDeclaredActivityStatusWhenPresent() {
    BddLogger.given("an activity detail data with a declared activity status");
    UUID id = UUID.randomUUID();
    UUID declaredActivityId = UUID.randomUUID();
    UUID bannerId = UUID.randomUUID();
    String baseUrl = "https://cdn.example.com/";
    var activity =
        ActivityFixture.create()
            .withId(id)
            .withTitle("Activity Title")
            .withThematic(EActivityThematic.EXPERIENCES)
            .toModel();
    var banner =
        new FileData(Optional.of(bannerId), Optional.of("banner.png"), "banners/banner.png");
    ActivityPresentationData data =
        new ActivityPresentationData(
            activity.getId(),
            activity.getTitle(),
            activity.getThematic(),
            activity.getStatus(),
            Optional.of(declaredActivityId),
            Optional.of(EDeclaredActivityStatus.UNSUBSCRIBED),
            activity.getSummary(),
            activity.getDescription(),
            activity.getRecommendedCompletionContexts().orElse(null),
            banner,
            Optional.empty(),
            List.of(),
            activity.getCreatedAt(),
            activity.getUpdatedAt());

    BddLogger.when("mapping to ActivityDetailsDTO with base URL");
    ActivityPresentationDTO dto = mapper.toDTO(data, baseUrl);

    BddLogger.then("it should map the declared activity id and its status");
    assertEquals(declaredActivityId, dto.subscribedDeclaredActivity());
    assertEquals(EDeclaredActivityStatus.UNSUBSCRIBED, dto.subscribedDeclaredActivityStatus());
  }

  @Test
  void shouldMapTheSubActivitiesWithTheirPeriod() {
    BddLogger.given("an activity detail data listing a dated sub-activity and an undated one");
    UUID datedId = UUID.randomUUID();
    UUID undatedId = UUID.randomUUID();
    var activity = ActivityFixture.create().toModel();
    ActivityPresentationData data =
        new ActivityPresentationData(
            activity.getId(),
            activity.getTitle(),
            activity.getThematic(),
            activity.getStatus(),
            Optional.empty(),
            Optional.empty(),
            activity.getSummary(),
            activity.getDescription(),
            activity.getRecommendedCompletionContexts().orElse(null),
            new FileData(Optional.empty(), Optional.empty(), "banners/default.png"),
            Optional.empty(),
            List.of(
                new SubActivityOverviewData(
                    datedId,
                    "Dated step",
                    LocalDate.parse("2026-10-01"),
                    LocalDate.parse("2026-10-31"),
                    true),
                new SubActivityOverviewData(undatedId, "Undated step", null, null, false)),
            activity.getCreatedAt(),
            activity.getUpdatedAt());

    BddLogger.when("mapping to ActivityPresentationDTO");
    ActivityPresentationDTO dto = mapper.toDTO(data, "https://cdn.example.com/");

    BddLogger.then("each sub-activity should keep its period, or none");
    assertEquals(
        List.of(
            new SubActivityOverviewDTO(
                datedId,
                "Dated step",
                LocalDate.parse("2026-10-01"),
                LocalDate.parse("2026-10-31"),
                true),
            new SubActivityOverviewDTO(undatedId, "Undated step", null, null, false)),
        dto.subActivities());
  }
}
