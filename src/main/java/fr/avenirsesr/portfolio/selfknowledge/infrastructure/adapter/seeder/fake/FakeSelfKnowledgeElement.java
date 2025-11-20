package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryEntity;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeElementEntity;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder.data.CsvSelfKnowledgeElementDto;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder.data.SelfKnowledgeElementCSVData;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class FakeSelfKnowledgeElement {

  private final SelfKnowledgeElementEntity element;

  private static final Random random = new Random();

  private FakeSelfKnowledgeElement(SelfKnowledgeElementEntity element) {
    this.element = element;
  }

  public static FakeSelfKnowledgeElement of(
      StudentEntity student, SelfKnowledgeCategoryEntity category) {
    List<CsvSelfKnowledgeElementDto> elementDtos;

    switch (category.getType()) {
      case STRENGTHS -> elementDtos = SelfKnowledgeElementCSVData.getDataStrenghs();
      case VALUES -> elementDtos = SelfKnowledgeElementCSVData.getDataValues();
      default -> elementDtos = SelfKnowledgeElementCSVData.getDataAspirations();
    }
    CsvSelfKnowledgeElementDto elementDto = elementDtos.get(random.nextInt(elementDtos.size()));

    var entity =
        SelfKnowledgeElementEntity.of(
            UUID.randomUUID(),
            student,
            elementDto.title(),
            elementDto.description(),
            elementDto.rating(),
            category);

    return new FakeSelfKnowledgeElement(entity);
  }

  public SelfKnowledgeElementEntity toEntity() {
    return element;
  }
}
