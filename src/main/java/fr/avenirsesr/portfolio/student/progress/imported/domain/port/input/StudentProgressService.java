package fr.avenirsesr.portfolio.student.progress.imported.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.student.progress.imported.domain.data.SkillProgressData;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.Map;

public interface StudentProgressService {
  List<StudentProgress> findAllStudentProgressesByStudent(Student student);

  List<StudentProgress> findStudentProgressesBySkillLevelProgresses(
      List<SkillLevelProgress> skillLevelProgresses);

  boolean isStudentFollowingAPCProgram();

  List<StudentProgress> getAllCurrentStudentProgress();

  Map<StudentProgress, List<SkillLevelProgress>> getStudentProgressOverview();

  Map<StudentProgress, List<SkillLevelProgress>> getStudentProgressView(SortCriteria sortCriteria);

  PagedResult<SkillProgressData> getAllTimeSkillsView(
      SortCriteria sortCriteria, PageCriteria pageCriteria);

  List<Skill> getAllSkillList();
}
