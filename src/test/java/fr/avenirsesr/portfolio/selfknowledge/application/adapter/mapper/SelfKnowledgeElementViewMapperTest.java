package fr.avenirsesr.portfolio.selfknowledge.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.selfknowledge.application.adapter.dto.SelfKnowledgeElementViewDTO;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.fixture.SelfKnowledgeElementFixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class SelfKnowledgeElementViewMapperTest {

  private final SelfKnowledgeElementViewMapper mapper =
      Mappers.getMapper(SelfKnowledgeElementViewMapper.class);

  @Test
  void shouldMapSelfKnowledgeElementToViewDTO() {
    BddLogger.given("a self-knowledge element");
    SelfKnowledgeElement element = SelfKnowledgeElementFixture.create().toModel();

    BddLogger.when("mapping to SelfKnowledgeElementViewDTO");
    SelfKnowledgeElementViewDTO dto = mapper.toDTO(element);

    BddLogger.then("it should return a correct SelfKnowledgeElementViewDTO");
    assertNotNull(dto);
    assertEquals(element.getId(), dto.id());
    assertEquals(element.getTitle(), dto.title());
    assertEquals(element.getDescription(), dto.description());
    assertEquals(element.getRating(), dto.rating());
  }
}
