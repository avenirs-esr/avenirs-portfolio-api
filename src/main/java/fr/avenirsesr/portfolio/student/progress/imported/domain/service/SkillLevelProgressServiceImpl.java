package fr.avenirsesr.portfolio.student.progress.imported.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.input.SkillLevelProgressService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.output.repository.SkillLevelProgressRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class SkillLevelProgressServiceImpl implements SkillLevelProgressService {
  private final SkillLevelProgressRepository skillLevelProgressRepository;
  private final LoggedInUserService loggedInUserService;

  @Override
  public List<SkillLevelProgress> getSkillLevelsBySkillId(UUID skillId) {
    Student student = loggedInUserService.getLoggedInStudent();
    return skillLevelProgressRepository.findAllByStudentAndSkillId(student, skillId);
  }

  @Override
  public PagedResult<SkillLevelProgress> searchSkillLevel(
      String keyword, PageCriteria pageCriteria) {
    Student student = loggedInUserService.getLoggedInStudent();
    log.debug("Searching SkillLevelProgress for {} with pagination {}", student, pageCriteria);

    return skillLevelProgressRepository.findAllByStudent(student, pageCriteria, keyword);
  }
}
