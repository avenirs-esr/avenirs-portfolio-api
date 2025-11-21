package fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.UUID;

public interface SelfKnowledgeElementRepository
    extends GenericRepositoryPort<SelfKnowledgeElement> {
  PagedResult<SelfKnowledgeElement> findAllByStudentIdAndCategoryId(
      UUID studentId, UUID selfKnowledgeCategoryId, PageCriteria pageCriteria);

  void deleteAllByStudentAndCategory(Student student, SelfKnowledgeCategory category);
}
