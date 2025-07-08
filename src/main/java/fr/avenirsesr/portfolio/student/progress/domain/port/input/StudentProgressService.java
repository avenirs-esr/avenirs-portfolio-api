package fr.avenirsesr.portfolio.student.progress.domain.port.input;

import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.shared.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.Map;
import java.util.Set;

public interface StudentProgressService {
  boolean isStudentFollowingAPCProgram(Student student);

  Map<TrainingPath, Set<StudentProgress>> getSkillsOverview(Student student);

  Map<TrainingPath, Set<StudentProgress>> getSkillsView(Student student, SortCriteria sortCriteria);
}
