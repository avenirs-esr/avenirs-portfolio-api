package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityDetailsDTO;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityDetailData;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.data.FileData;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ActivityDetailsDtoMapperTest {

  private final ActivityDetailsDtoMapper mapper = Mappers.getMapper(ActivityDetailsDtoMapper.class);

  @Test
  void shouldMapActivityDetailDataToDTO() {
    BddLogger.given("an activity detail data with a banner");
    UUID id = UUID.randomUUID();
    UUID bannerId = UUID.randomUUID();
    String baseUrl = "https://cdn.example.com/";
    ActivityDetailData data =
        new ActivityDetailData(
            id,
            "Activity Title",
            EActivityThematic.EXPERIENCES,
            Optional.empty(),
            new FileData(Optional.of(bannerId), Optional.of("banner.png"), "banners/banner.png"),
            "Summary",
            "Description",
            "Every week",
            Instant.now(),
            Instant.now());

    BddLogger.when("mapping to ActivityDetailsDTO with base URL");
    ActivityDetailsDTO dto = mapper.toDTO(data, baseUrl);

    BddLogger.then("it should build the full banner URL and map all fields");
    assertNotNull(dto);
    assertEquals(id, dto.id());
    assertEquals("Activity Title", dto.title());
    assertEquals(EActivityThematic.EXPERIENCES, dto.thematic());
    assertNotNull(dto.banner());
    assertEquals(bannerId, dto.banner().fileId());
    assertEquals("banner.png", dto.banner().fileName());
    assertEquals(baseUrl + "banners/banner.png", dto.banner().url());
  }
}
