package fr.avenirsesr.portfolio.student.progress.imported.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.student.progress.imported.domain.data.SkillLevelProgressWithTraceCountData;
import fr.avenirsesr.portfolio.student.progress.imported.domain.data.SkillProgressData;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StudentProgressService {
  boolean isStudentFollowingAPCProgram();

  List<StudentProgress> getAllCurrentStudentProgress();

  Map<StudentProgress, List<SkillLevelProgressWithTraceCountData>> getStudentProgressOverview();

  Map<StudentProgress, List<SkillLevelProgressWithTraceCountData>> getStudentProgressView(
      SortCriteria sortCriteria);

  PagedResult<SkillProgressData> getAllTimeSkillsView(
      SortCriteria sortCriteria, PageCriteria pageCriteria);

  PagedResult<SkillLevelProgress> searchSkillLevel(String keyword, PageCriteria pageCriteria);

  List<SkillLevelProgress> getSkillLevelsBySkillId(UUID skillId);

  List<Skill> getAllSkillList();
}
