package fr.avenirsesr.portfolio.student.progress.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.student.progress.domain.data.SkillLevelProgressWithTraceCountData;
import fr.avenirsesr.portfolio.student.progress.domain.data.SkillProgressData;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StudentProgressService {
  boolean isStudentFollowingAPCProgram(Student student);

  Map<StudentProgress, List<SkillLevelProgressWithTraceCountData>> getStudentProgressOverview(
      Student student);

  Map<StudentProgress, List<SkillLevelProgressWithTraceCountData>> getStudentProgressView(
      Student student, SortCriteria sortCriteria);

  PagedResult<SkillProgressData> getAllTimeSkillsView(
      Student student, SortCriteria sortCriteria, PageCriteria pageCriteria);

  PagedResult<SkillLevelProgress> search(
      Student student, String keyword, PageCriteria pageCriteria);

  List<SkillLevelProgress> getSkillLevelsBySkillId(Student student, UUID skillId);

  List<Skill> getAllSkillList(Student student);
}
