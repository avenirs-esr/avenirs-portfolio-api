package fr.avenirsesr.portfolio.selfknowledge.domain.port.input;

import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import java.util.List;

public interface SelfKnowledgeService {
  List<SelfKnowledgeCategory> getSelfKnowledgeCategories();

  List<SelfKnowledgeCategory> getSelfKnowledgeCategoriesAvailable();
}
