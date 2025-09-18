package fr.avenirsesr.portfolio.additionalskill.domain.service;

import fr.avenirsesr.portfolio.additionalskill.domain.exception.AdditionalSkillNotFoundException;
import fr.avenirsesr.portfolio.additionalskill.domain.exception.DuplicateAdditionalSkillException;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.input.AdditionalSkillService;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillProgressRepository;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class AdditionalSkillServiceImpl implements AdditionalSkillService {
  private final AdditionalSkillRepository additionalSkillRepository;
  private final AdditionalSkillProgressRepository additionalSkillProgressRepository;

  @Override
  public PagedResult<AdditionalSkillProgress> getAdditionalSkillsProgresses(
      Student student, PageCriteria pageCriteria) {
    return additionalSkillProgressRepository.findAllByStudent(student, pageCriteria);
  }

  @Override
  public void createAdditionalSkillProgress(
      Student student,
      UUID additionalSkillId,
      EAdditionalSkillType type,
      EAdditionalSkillLevel level) {
    try {
      Optional<AdditionalSkill> additionalSkill =
          additionalSkillRepository.findById(additionalSkillId);
      AdditionalSkillProgress additionalSkillProgress =
          AdditionalSkillProgress.create(
              student, additionalSkill.orElseThrow(AdditionalSkillNotFoundException::new), level);
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
