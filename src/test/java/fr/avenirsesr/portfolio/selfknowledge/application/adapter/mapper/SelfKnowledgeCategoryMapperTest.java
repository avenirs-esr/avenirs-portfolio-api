package fr.avenirsesr.portfolio.selfknowledge.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.selfknowledge.application.adapter.dto.SelfKnowledgeCategoryDTO;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.fixture.SelfKnowledgeCategoryFixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class SelfKnowledgeCategoryMapperTest {

  private final SelfKnowledgeCategoryMapper mapper =
      Mappers.getMapper(SelfKnowledgeCategoryMapper.class);

  @Test
  void shouldMapSelfKnowledgeCategoryToDTO() {
    BddLogger.given("a self-knowledge category");
    SelfKnowledgeCategory category = SelfKnowledgeCategoryFixture.create().toModel();

    BddLogger.when("mapping to SelfKnowledgeCategoryDTO");
    SelfKnowledgeCategoryDTO dto = mapper.toSelfKnowledgeCategoryDTO(category);

    BddLogger.then("it should return a correct SelfKnowledgeCategoryDTO");
    assertNotNull(dto);
    assertEquals(category.getId(), dto.id());
    assertEquals(category.getTitle(), dto.title());
    assertEquals(category.getDescription(), dto.description());
    assertEquals(category.getType(), dto.type());
  }
}
