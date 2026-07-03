package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityPresentationDTO;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityPresentationData;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.data.FileData;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;
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
    var files =
        List.of(
            new FileDTO(UUID.randomUUID(), "file1.pdf", baseUrl + "files/file1.pdf"),
            new FileDTO(UUID.randomUUID(), "file2.docx", baseUrl + "files/file2.docx"));
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
            activity.getLinks(),
            activity.getCreatedAt(),
            activity.getUpdatedAt());

    BddLogger.when("mapping to ActivityDetailsDTO with base URL");
    ActivityPresentationDTO dto = mapper.toDTO(data, baseUrl, files);

    BddLogger.then("it should build the full banner URL and map all fields");
    assertNotNull(dto);
    assertEquals(id, dto.id());
    assertEquals("Activity Title", dto.title());
    assertEquals(EActivityThematic.EXPERIENCES, dto.thematic());
    assertNotNull(dto.banner());
    assertEquals(bannerId, dto.banner().id());
    assertEquals("banner.png", dto.banner().fileName());
    assertEquals(baseUrl + "banners/banner.png", dto.banner().url());
    assertEquals(2, dto.files().size());
    assertEquals(baseUrl + "files/file1.pdf", dto.files().get(0).url());
    assertEquals(baseUrl + "files/file2.docx", dto.files().get(1).url());
    assertEquals(activity.getCreatedAt(), dto.createdAt());
    assertEquals(activity.getUpdatedAt(), dto.updatedAt());
    assertEquals(activity.getLinks(), dto.links());
  }
}
