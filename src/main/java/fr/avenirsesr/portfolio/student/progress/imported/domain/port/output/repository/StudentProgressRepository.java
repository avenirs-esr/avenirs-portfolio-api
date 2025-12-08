package fr.avenirsesr.portfolio.student.progress.imported.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;

public interface StudentProgressRepository extends GenericRepositoryPort<StudentProgress> {
  List<StudentProgress> findAllByStudent(Student student);

  List<StudentProgress> findAllAPCByStudent(Student student);

  List<StudentProgress> findStudentProgressesBySkillLevelProgresses(
      List<SkillLevelProgress> skillLevelProgresses);
}
