package fr.avenirsesr.portfolio.user.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;

public interface StudentRepository extends GenericRepositoryPort<Student> {
  void addSelfKnowledgeCategories(Student student, List<SelfKnowledgeCategory> categories);
}
