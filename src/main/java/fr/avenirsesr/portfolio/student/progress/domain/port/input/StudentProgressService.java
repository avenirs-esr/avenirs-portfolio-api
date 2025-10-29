package fr.avenirsesr.portfolio.student.progress.domain.port.input;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.student.progress.domain.data.AdditionalSkillProgressDetails;
import fr.avenirsesr.portfolio.student.progress.domain.data.SkillLevelProgressWithTraceCountData;
import fr.avenirsesr.portfolio.student.progress.domain.data.SkillProgressData;
import fr.avenirsesr.portfolio.student.progress.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StudentProgressService {
  boolean isStudentFollowingAPCProgram();

  Map<StudentProgress, List<SkillLevelProgressWithTraceCountData>> getStudentProgressOverview();

  Map<StudentProgress, List<SkillLevelProgressWithTraceCountData>> getStudentProgressView(
      SortCriteria sortCriteria);

  PagedResult<SkillProgressData> getAllTimeSkillsView(
      SortCriteria sortCriteria, PageCriteria pageCriteria);

  PagedResult<SkillLevelProgress> searchSkillLevel(String keyword, PageCriteria pageCriteria);

  List<SkillLevelProgress> getSkillLevelsBySkillId(UUID skillId);

  List<Skill> getAllSkillList();

  PagedResult<AdditionalSkillProgress> getAdditionalSkillsProgresses(PageCriteria criteria);

  AdditionalSkillProgress createAdditionalSkillProgress(
      UUID additionalSkillId,
      EAdditionalSkillType type,
      EAdditionalSkillLevel level,
      String description);

  AdditionalSkillProgress updateAdditionalSkillProgress(
      UUID additionalSkillProgressId, EAdditionalSkillLevel level, String description);

  PagedResult<AdditionalSkillProgress> searchAdditionalSkill(
      String keyword, PageCriteria pageCriteria);

  AdditionalSkillProgressDetails getAdditionalSkillProgressDetails(UUID additionalSkillProgressId);
}
