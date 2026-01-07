package fr.avenirsesr.portfolio.selfknowledge.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.selfknowledge.domain.data.SelfKnowledgeElementDetails;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.UUID;

public interface SelfKnowledgeService {
  PagedResult<SelfKnowledgeElement> getSelfKnowledgeElements(
      UUID selfKnowledgeCategoryId, PageCriteria pageCriteria);

  SelfKnowledgeElementDetails getSelfKnowledgeElementDetails(UUID selfKnowledgeElementId);

  SelfKnowledgeElement createSelfKnowledgeElement(
      UUID selfKnowledgeCategoryId, String title, String description, Integer rating);

  SelfKnowledgeElement updateSelfKnowledgeElement(
      UUID selfKnowledgeElementId, String title, String description, Integer rating);

  void deleteSelfKnowledgeElements(List<UUID> selfKnowledgeElementIds);

  List<SelfKnowledgeCategory> getSelfKnowledgeCategories();

  void initSelfKnowledgeCategoriesMandatory(Student student);

  List<SelfKnowledgeCategory> getSelfKnowledgeCategoriesAvailable();

  void addSelfKnowledgeCategories(List<String> categoriesId);

  void removeSelfKnowledgeCategory(UUID categoryId);
}
