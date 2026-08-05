package fr.avenirsesr.portfolio.student.selfknowledge.application.adapter.mapper;

import static org.junit.jupiter.api.Assertions.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.selfknowledge.application.adapter.dto.SelfKnowledgeElementViewDTO;
import fr.avenirsesr.portfolio.student.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.student.selfknowledge.infrastructure.fixture.SelfKnowledgeElementFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SelfKnowledgeElementViewMapperTest {

  @Spy
  private SelfKnowledgeCategoryMapper selfKnowledgeCategoryMapper =
      Mappers.getMapper(SelfKnowledgeCategoryMapper.class);

  @InjectMocks private SelfKnowledgeElementViewMapperImpl mapper;

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
    assertEquals(element.isValorized(), dto.valorized());
    assertNotNull(dto.category());
    assertEquals(element.getSelfKnowledgeCategory(), dto.category().type());
    assertEquals(element.getSelfKnowledgeCategory().isMandatory(), dto.category().mandatory());
  }
}
