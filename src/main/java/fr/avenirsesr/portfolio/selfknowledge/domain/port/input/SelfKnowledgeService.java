package fr.avenirsesr.portfolio.selfknowledge.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import java.util.List;
import java.util.UUID;

public interface SelfKnowledgeService {
  PagedResult<SelfKnowledgeElement> getSelfKnowledgeElements(
      UUID selfKnowledgeCategoryId, PageCriteria pageCriteria);

  List<SelfKnowledgeCategory> getSelfKnowledgeCategories();

  List<SelfKnowledgeCategory> getSelfKnowledgeCategoriesAvailable();

  void addSelfKnowledgeCategories(List<String> categoriesId);
}
