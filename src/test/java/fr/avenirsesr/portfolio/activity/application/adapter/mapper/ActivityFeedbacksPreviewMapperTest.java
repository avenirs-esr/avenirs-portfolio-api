package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityFeedbacksPreviewDTO;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.mapper.ActivityFeedbacksPreviewMapper;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ActivityFeedbacksPreviewMapperTest {

  private final ActivityFeedbacksPreviewMapper mapper =
      Mappers.getMapper(ActivityFeedbacksPreviewMapper.class);

  @Test
  void shouldMapActivityToFeedbacksPreviewDTO() {
    BddLogger.given("an activity");
    Activity activity = ActivityFixture.create().toModel();

    BddLogger.when("mapping to ActivityItemNavigationDTO");
    ActivityFeedbacksPreviewDTO dto = mapper.toDTO(activity);

    BddLogger.then("it should return a correct ActivityItemNavigationDTO");
    assertNotNull(dto);
    assertEquals(activity.getId(), dto.id());
    assertEquals(activity.getTitle(), dto.title());
    assertEquals(activity.getDescription(), dto.description());
  }
}
