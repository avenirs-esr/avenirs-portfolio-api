package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import java.util.List;

public interface ProgramProgressRepository extends GenericRepositoryPort<ProgramProgress> {
  List<ProgramProgress> findAllByStudent(Student student, ELanguage language);

  List<ProgramProgress> findAllAPCByStudent(Student student);

  List<ProgramProgress> findAllWithoutSkillsByStudent(Student student, ELanguage language);
}
