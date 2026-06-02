package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityPresentationDTO;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityPresentationData;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.data.FileData;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ActivityPresentationDtoMapperTest {

  private final ActivityPresentationDtoMapper mapper =
      Mappers.getMapper(ActivityPresentationDtoMapper.class);

  @Test
  void shouldMapActivityDetailDataToDTO() {
    BddLogger.given("an activity detail data with a banner");
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
            Optional.empty(),
            activity.getSummary(),
            activity.getDescription().orElse(null),
            activity.getExecutionPeriodInfo().orElse(null),
            banner,
            activity.getCreatedAt(),
            activity.getUpdatedAt());

    BddLogger.when("mapping to ActivityDetailsDTO with base URL");
    ActivityPresentationDTO dto = mapper.toDTO(data, baseUrl);

    BddLogger.then("it should build the full banner URL and map all fields");
    assertNotNull(dto);
    assertEquals(id, dto.id());
    assertEquals("Activity Title", dto.title());
    assertEquals(EActivityThematic.EXPERIENCES, dto.thematic());
    assertNotNull(dto.banner());
    assertEquals(bannerId, dto.banner().id());
    assertEquals("banner.png", dto.banner().fileName());
    assertEquals(baseUrl + "banners/banner.png", dto.banner().url());
  }
}
