package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import java.util.List;

public interface ProgramProgressRepository extends GenericRepositoryPort<ProgramProgress> {
  List<ProgramProgress> findAllByStudent(Student student);

  List<ProgramProgress> findAllByStudentAndLearningMethod(
      Student student, EPortfolioType learningMethod);
}
