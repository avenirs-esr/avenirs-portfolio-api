package fr.avenirsesr.portfolio.selfknowledge.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;

public interface SelfKnowledgeCategoryRepository
    extends GenericRepositoryPort<SelfKnowledgeCategory> {
  List<SelfKnowledgeCategory> findAllByStudent(Student student);

  List<SelfKnowledgeCategory> findAllAvailableByStudent(Student student);
}
