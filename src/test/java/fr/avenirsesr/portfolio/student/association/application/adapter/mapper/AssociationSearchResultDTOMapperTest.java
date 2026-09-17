package fr.avenirsesr.portfolio.student.association.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationSearchResultDTO;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class AssociationSearchResultDTOMapperTest {

  private final AssociationSearchResultDTOMapper mapper =
      Mappers.getMapper(AssociationSearchResultDTOMapper.class);

  @Test
  void shouldMapToDTO() {
    BddLogger.given("an association search result");
    UUID id = UUID.randomUUID();
    AssociationSearchResultData data =
        new AssociationSearchResultData(
            id, "Java Workshop", EActivityThematic.EXPERIENCES.name(), true);

    BddLogger.when("mapping to DTO");
    AssociationSearchResultDTO dto = mapper.toDTO(data);

    BddLogger.then("it should return the same fields");
    assertNotNull(dto);
    assertEquals(id, dto.id());
    assertEquals("Java Workshop", dto.title());
    assertEquals(EActivityThematic.EXPERIENCES.name(), dto.category());
    assertTrue(dto.disabled());
  }

  @Test
  void shouldHandleNullCategory() {
    BddLogger.given("an association search result without category");
    AssociationSearchResultData data =
        new AssociationSearchResultData(UUID.randomUUID(), "My Trace", null, false);

    BddLogger.when("mapping to DTO");
    AssociationSearchResultDTO dto = mapper.toDTO(data);

    BddLogger.then("it should return null for category");
    assertNull(dto.category());
    assertFalse(dto.disabled());
  }
}
