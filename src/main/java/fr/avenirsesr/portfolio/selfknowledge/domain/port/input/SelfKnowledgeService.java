package fr.avenirsesr.portfolio.selfknowledge.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.selfknowledge.domain.data.SelfKnowledgeElementDetails;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import java.util.List;
import java.util.UUID;

public interface SelfKnowledgeService {
  PagedResult<SelfKnowledgeElement> getSelfKnowledgeElements(
      ESelfKnowledgeCategory selfKnowledgeCategory, PageCriteria pageCriteria, Boolean isValorized);

  SelfKnowledgeElementDetails getSelfKnowledgeElementDetails(UUID selfKnowledgeElementId);

  SelfKnowledgeElement createSelfKnowledgeElement(
      ESelfKnowledgeCategory selfKnowledgeCategory,
      String title,
      String description,
      Integer rating);

  SelfKnowledgeElement updateSelfKnowledgeElement(
      UUID selfKnowledgeElementId,
      String title,
      String description,
      Integer rating,
      boolean valorized);

  void deleteSelfKnowledgeElements(List<UUID> selfKnowledgeElementIds);

  List<ESelfKnowledgeCategory> getSelfKnowledgeCategories();

  List<ESelfKnowledgeCategory> getSelfKnowledgeCategoriesAvailable();

  void addSelfKnowledgeCategories(List<ESelfKnowledgeCategory> categories);

  void removeSelfKnowledgeCategory(ESelfKnowledgeCategory selfKnowledgeCategory);
}
