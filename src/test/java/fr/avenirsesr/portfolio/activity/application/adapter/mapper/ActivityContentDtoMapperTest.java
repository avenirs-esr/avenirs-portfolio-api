package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityContentDTO;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityContentData;
import fr.avenirsesr.portfolio.activity.domain.mapper.ActivityContentDataMapper;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.data.FileData;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ActivityContentDtoMapperTest {

  private final ActivityContentDtoMapper mapper = Mappers.getMapper(ActivityContentDtoMapper.class);

  @Test
  void shouldMapActivityToDTO() {
    BddLogger.given("an activity content data with a banner");

    String baseUrl = "https://cdn.example.com/";
    Activity activity =
        ActivityFixture.create()
            .withId(UUID.randomUUID())
            .withTitle("Activity Title")
            .withThematic(EActivityThematic.EXPERIENCES)
            .toModel();
    FileData bannerData =
        new FileData(
            Optional.of(UUID.randomUUID()), Optional.of("banner.png"), "banners/banner.png");
    ActivityContentData activityContentData =
        ActivityContentDataMapper.toData(activity, bannerData);

    BddLogger.when("mapping to ActivityContentDTO with base URL");
    ActivityContentDTO dto = mapper.toDTO(activityContentData, baseUrl);

    BddLogger.then("it should build the full banner URL and map all fields");
    assertNotNull(dto);
    assertEquals(activity.getId(), dto.id());
    assertEquals(activity.getTitle(), dto.title());
    assertEquals(activity.getThematic(), dto.thematic());
    assertNotNull(dto.banner());
    assertEquals(bannerData.id().orElse(null), dto.banner().fileId());
    assertEquals(bannerData.name().orElse(null), dto.banner().fileName());
    assertEquals(baseUrl + bannerData.url(), dto.banner().url());
  }
}
