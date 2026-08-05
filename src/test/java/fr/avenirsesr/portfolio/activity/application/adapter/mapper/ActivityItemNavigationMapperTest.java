package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityItemNavigationDTO;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.mapper.ActivityItemNavigationMapper;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ActivityItemNavigationMapperTest {

  private final ActivityItemNavigationMapper mapper =
      Mappers.getMapper(ActivityItemNavigationMapper.class);

  @Test
  void shouldMapActivityToItemNavigationDTO() {
    BddLogger.given("an activity");
    Activity activity = ActivityFixture.create().toModel();

    BddLogger.when("mapping to ActivityItemNavigationDTO");
    ActivityItemNavigationDTO dto = mapper.toDTO(activity);

    BddLogger.then("it should return a correct ActivityItemNavigationDTO");
    assertNotNull(dto);
    assertEquals(activity.getId(), dto.id());
    assertEquals(activity.getTitle(), dto.title());
  }
}
