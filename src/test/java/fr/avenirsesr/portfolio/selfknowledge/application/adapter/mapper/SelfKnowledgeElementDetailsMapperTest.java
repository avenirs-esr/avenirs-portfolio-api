package fr.avenirsesr.portfolio.selfknowledge.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.selfknowledge.application.adapter.dto.SelfKnowledgeElementDetailsDTO;
import fr.avenirsesr.portfolio.student.selfknowledge.application.adapter.mapper.SelfKnowledgeElementDetailsMapper;
import fr.avenirsesr.portfolio.student.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.fixture.SelfKnowledgeElementFixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class SelfKnowledgeElementDetailsMapperTest {

  private final SelfKnowledgeElementDetailsMapper mapper =
      Mappers.getMapper(SelfKnowledgeElementDetailsMapper.class);

  @Test
  void shouldMapSelfKnowledgeElementToDetailsDTO() {
    BddLogger.given("a self-knowledge element");
    SelfKnowledgeElement element = SelfKnowledgeElementFixture.create().toModel();

    BddLogger.when("mapping to SelfKnowledgeElementDetailsDTO");
    SelfKnowledgeElementDetailsDTO dto = mapper.toDTO(element);

    BddLogger.then("it should return a correct SelfKnowledgeElementDetailsDTO");
    assertNotNull(dto);
    assertEquals(element.getId(), dto.id());
    assertEquals(element.getTitle(), dto.title());
    assertEquals(element.getDescription(), dto.description());
    assertEquals(element.getRating(), dto.rating());
    assertEquals(element.isValorized(), dto.valorized());
  }
}
