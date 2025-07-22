package fr.avenirsesr.portfolio.additionalskill.domain.service;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.port.input.AdditionalSkillService;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.AdditionalSkillCache;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.additionalskill.domain.exception.AdditionalSkillNotAvailableException;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillsPaginated;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.input.AdditionalSkillService;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.AdditionalSkillCache;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.StudentAdditionalSkillRepository;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class AdditionalSkillServiceImpl implements AdditionalSkillService {
  private final AdditionalSkillCache additionalSkillCache;
  private final StudentAdditionalSkillRepository studentAdditionalSkillRepository;

  @Override
  public PagedResult<AdditionalSkill> getAdditionalSkills(PageCriteria pageCriteria) {
    return additionalSkillCache.findAll(pageCriteria);
  }

  @Override
  public PagedResult<AdditionalSkill> searchAdditionalSkills(
      String keyword, PageCriteria pageCriteria) {
    return additionalSkillCache.findBySkillTitle(keyword, pageCriteria);
  }

  @Override
  public void saveAdditionalSkills(
      Student student,
      String additionalSkillId,
      EAdditionalSkillType type,
      ESkillLevelStatus level) {
    if (additionalSkillCache.additionalSkillIsAvailable(additionalSkillId)) {
      studentAdditionalSkillRepository.saveAdditionalSkill(student, additionalSkillId, type, level);
    } else {
      log.warn("Additional skill with ID [{}] is not available", additionalSkillId);
      throw new AdditionalSkillNotAvailableException();
    }
  }
}
