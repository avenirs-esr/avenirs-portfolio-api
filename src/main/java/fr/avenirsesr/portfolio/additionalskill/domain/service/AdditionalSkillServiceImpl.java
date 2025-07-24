package fr.avenirsesr.portfolio.additionalskill.domain.service;

import fr.avenirsesr.portfolio.additionalskill.domain.exception.AdditionalSkillNotFoundException;
import fr.avenirsesr.portfolio.additionalskill.domain.exception.DuplicateAdditionalSkillException;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.input.AdditionalSkillService;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.AdditionalSkillCache;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillProgressRepository;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class AdditionalSkillServiceImpl implements AdditionalSkillService {
  private final AdditionalSkillCache additionalSkillCache;
  private final AdditionalSkillProgressRepository additionalSkillProgressRepository;

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
  public void addAdditionalSkills(
      Student student,
      UUID additionalSkillId,
      EAdditionalSkillType type,
      EAdditionalSkillLevel level) {
    try {
      AdditionalSkill additionalSkill = additionalSkillCache.findById(additionalSkillId);
      AdditionalSkillProgress additionalSkillProgress =
          AdditionalSkillProgress.create(student, additionalSkill, level);
      if (additionalSkillProgressRepository.additionalSkillProgressAlreadyExists(
          additionalSkillProgress)) {
        log.error(
            "Failed to add additional skill [{}] for student [{}] because it already exists",
            additionalSkillId,
            student);
        throw new DuplicateAdditionalSkillException();
      }
      additionalSkillProgressRepository.save(additionalSkillProgress);
    } catch (AdditionalSkillNotFoundException e) {
      log.error("Failed to add additional skill for student [{}]: {}", student, e.getMessage());
      throw e;
    }
  }
}
