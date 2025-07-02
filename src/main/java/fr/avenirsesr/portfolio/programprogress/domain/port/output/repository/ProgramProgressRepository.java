package fr.avenirsesr.portfolio.programprogress.domain.port.output.repository;

import fr.avenirsesr.portfolio.programprogress.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import fr.avenirsesr.portfolio.shared.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.shared.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;

public interface ProgramProgressRepository extends GenericRepositoryPort<ProgramProgress> {
  List<ProgramProgress> findAllByStudent(Student student);

  List<ProgramProgress> findAllByStudent(Student student, SortCriteria sortCriteria);

  List<ProgramProgress> findAllByStudent(Student student, PageInfo pageInfo);

  List<ProgramProgress> findAllAPCByStudent(Student student);

  List<ProgramProgress> findAllWithoutSkillsByStudent(Student student);
}
