package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityNavigationDTO;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.mapper.ActivityItemNavigationMapper;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivityNavigationMapperTest {

  @Spy
  private ActivityItemNavigationMapper activityItemNavigationMapper =
      Mappers.getMapper(ActivityItemNavigationMapper.class);

  @InjectMocks private ActivityNavigationMapperImpl mapper;

  @Test
  void shouldMapActivitiesByThematicToNavigationDTOs() {
    BddLogger.given("a map of activities grouped by thematic");
    Activity activity =
        ActivityFixture.create().withThematic(EActivityThematic.EXPERIENCES).toModel();
    Map<EActivityThematic, List<Activity>> byThematic =
        Map.of(EActivityThematic.EXPERIENCES, List.of(activity));

    BddLogger.when("mapping to ActivityNavigationDTO list");
    List<ActivityNavigationDTO> dtos = mapper.toDTO(byThematic);

    BddLogger.then("it should return one navigation DTO per thematic with correct items");
    assertNotNull(dtos);
    assertEquals(1, dtos.size());
    assertEquals(EActivityThematic.EXPERIENCES.name(), dtos.get(0).title());
    assertEquals(1, dtos.get(0).items().size());
    assertEquals(activity.getId(), dtos.get(0).items().get(0).id());
  }

  @Test
  void shouldReturnEmptyListWhenMapIsEmpty() {
    BddLogger.given("an empty activities map");

    BddLogger.when("mapping to ActivityNavigationDTO list");
    List<ActivityNavigationDTO> dtos = mapper.toDTO(Map.of());

    BddLogger.then("it should return an empty list");
    assertNotNull(dtos);
    assertTrue(dtos.isEmpty());
  }

  @Test
  void shouldReturnEmptyListWhenMapIsNull() {
    BddLogger.given("a null activities map");

    BddLogger.when("mapping to ActivityNavigationDTO list");
    List<ActivityNavigationDTO> dtos = mapper.toDTO(null);

    BddLogger.then("it should return an empty list");
    assertNotNull(dtos);
    assertTrue(dtos.isEmpty());
  }
}
