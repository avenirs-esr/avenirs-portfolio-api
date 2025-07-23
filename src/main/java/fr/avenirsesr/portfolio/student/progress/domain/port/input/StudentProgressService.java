package fr.avenirsesr.portfolio.student.progress.domain.port.input;

import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.Map;

public interface StudentProgressService {
  boolean isStudentFollowingAPCProgram(Student student);

  Map<StudentProgress, List<SkillLevelProgress>> getStudentProgressOverview(Student student);

  List<StudentProgress> getStudentProgressView(Student student, SortCriteria sortCriteria);

  PagedResult<SkillProgress> getAllTimeSkillsView(
      Student student, SortCriteria sortCriteria, PageCriteria pageCriteria);
}
