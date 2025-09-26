package fr.avenirsesr.portfolio.student.progress.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.student.progress.domain.dto.SkillLevelProgressWithTraceCountDTO;
import fr.avenirsesr.portfolio.student.progress.domain.dto.SkillProgressDTO;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StudentProgressService {
  boolean isStudentFollowingAPCProgram(Student student);

  Map<StudentProgress, List<SkillLevelProgressWithTraceCountDTO>> getStudentProgressOverview(
      Student student);

  Map<StudentProgress, List<SkillLevelProgressWithTraceCountDTO>> getStudentProgressView(
      Student student, SortCriteria sortCriteria);

  PagedResult<SkillProgressDTO> getAllTimeSkillsView(
      Student student, SortCriteria sortCriteria, PageCriteria pageCriteria);

  PagedResult<SkillLevelProgress> search(
      Student student, String keyword, PageCriteria pageCriteria);

  List<SkillLevelProgress> getSkillLevelsBySkillId(Student student, UUID skillId);
}
