package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityContentDTO;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivityContentDtoMapperTest {

  @Spy private OptionalMapper optionalMapper = Mappers.getMapper(OptionalMapper.class);

  @InjectMocks private ActivityContentDtoMapperImpl mapper;

  @Test
  void shouldMapActivityToDTO() {
    BddLogger.given("an activity");
    Activity activity = ActivityFixture.create().toModel();

    BddLogger.when("mapping to ActivityContentDTO");
    ActivityContentDTO dto = mapper.toDTO(activity);

    BddLogger.then("it should return a correct ActivityContentDTO");
    assertNotNull(dto);
    assertEquals(activity.getId(), dto.id());
    assertEquals(activity.getTitle(), dto.title());
    assertEquals(activity.getThematic(), dto.thematic());
    assertEquals(activity.getSummary(), dto.summary());
    assertEquals(activity.getDescription().orElse(null), dto.description());
    assertEquals(activity.getExecutionPeriodInfo().orElse(null), dto.executionPeriodInfo());
  }

  @Test
  void shouldMapEmptyOptionalFieldsToNull() {
    BddLogger.given("an activity with empty optional fields");
    Activity activity =
        ActivityFixture.create().withDescription(null).withExecutionPeriodInfo(null).toModel();

    BddLogger.when("mapping to ActivityContentDTO");
    ActivityContentDTO dto = mapper.toDTO(activity);

    BddLogger.then("optional fields should be mapped to null");
    assertNotNull(dto);
    assertNull(dto.description());
    assertNull(dto.executionPeriodInfo());
  }
}
