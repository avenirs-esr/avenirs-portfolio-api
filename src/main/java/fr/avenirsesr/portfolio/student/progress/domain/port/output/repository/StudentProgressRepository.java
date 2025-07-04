package fr.avenirsesr.portfolio.student.progress.domain.port.output.repository;

import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import fr.avenirsesr.portfolio.shared.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.shared.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;

public interface StudentProgressRepository extends GenericRepositoryPort<StudentProgress> {
  List<StudentProgress> findAllByStudent(Student student);

  List<StudentProgress> findAllByStudent(Student student, SortCriteria sortCriteria);

  List<StudentProgress> findAllByStudent(Student student, PageInfo pageInfo);

  List<StudentProgress> findAllAPCByStudent(Student student);

  List<TrainingPath> findAllWithoutSkillsByStudent(Student student);
}
