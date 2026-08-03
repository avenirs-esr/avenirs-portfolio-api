package fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.UUID;

public interface SelfKnowledgeElementRepository
    extends GenericRepositoryPort<SelfKnowledgeElement> {
  PagedResult<SelfKnowledgeElement> findAllByStudentIdAndCategories(
      UUID studentId,
      List<ESelfKnowledgeCategory> selfKnowledgeCategories,
      PageCriteria pageCriteria,
      Boolean isValorized);

  void deleteAllByStudentAndCategory(Student student, ESelfKnowledgeCategory selfKnowledgeCategory);
}
