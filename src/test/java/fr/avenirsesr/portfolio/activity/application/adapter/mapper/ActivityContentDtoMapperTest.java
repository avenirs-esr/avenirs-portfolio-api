package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityContentDTO;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ActivityContentDtoMapperTest {

  private final ActivityContentDtoMapper mapper = Mappers.getMapper(ActivityContentDtoMapper.class);

  @Test
  void shouldMapActivityToDTO() {
    BddLogger.given("an activity");
    Activity activity = ActivityFixture.create().toModel();

    BddLogger.when("mapping to ActivityDTO");
    ActivityContentDTO dto = mapper.toDTO(activity);

    BddLogger.then("it should return a correct ActivityDTO");
    assertNotNull(dto);
    assertEquals(activity.getId(), dto.id());
    assertEquals(activity.getTitle(), dto.title());
    assertEquals(activity.getThematic(), dto.thematic());
    assertEquals(activity.getSummary(), dto.summary());
    assertEquals(activity.getDescription().get(), dto.description());
    assertEquals(activity.getExecutionPeriodInfo().get(), dto.executionPeriodInfo());
  }
}
