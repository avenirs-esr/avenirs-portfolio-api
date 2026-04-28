package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityOverviewDTO;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityWithStudentStatusData;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ActivityOverviewDtoMapperTest {

  private final ActivityOverviewDtoMapper mapper =
      Mappers.getMapper(ActivityOverviewDtoMapper.class);

  @Test
  void shouldMapActivityWithStatusToOverviewDTO() {
    BddLogger.given("an activity with student status data");
    Activity activity = ActivityFixture.create().toModel();
    ActivityWithStudentStatusData data =
        new ActivityWithStudentStatusData(activity, true, EDeclaredActivityStatus.SUBSCRIBED);

    BddLogger.when("mapping to ActivityOverviewDTO");
    ActivityOverviewDTO dto = mapper.toDTO(data);

    BddLogger.then("it should map nested activity fields correctly");
    assertNotNull(dto);
    assertEquals(activity.getId(), dto.id());
    assertEquals(activity.getTitle(), dto.title());
    assertEquals(activity.getThematic(), dto.thematic());
    assertEquals(activity.getSummary(), dto.summary());
    assertEquals(EDeclaredActivityStatus.SUBSCRIBED, dto.status());
    assertTrue(dto.isNew());
  }
}
