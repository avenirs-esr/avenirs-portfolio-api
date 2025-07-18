package fr.avenirsesr.portfolio.student.progress.domain.port.input;

import fr.avenirsesr.portfolio.shared.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.Map;

public interface StudentProgressService {
  boolean isStudentFollowingAPCProgram(Student student);

  Map<StudentProgress, List<SkillLevelProgress>> getSkillsOverview(Student student);

  List<StudentProgress> getSkillsView(Student student, SortCriteria sortCriteria);
}
