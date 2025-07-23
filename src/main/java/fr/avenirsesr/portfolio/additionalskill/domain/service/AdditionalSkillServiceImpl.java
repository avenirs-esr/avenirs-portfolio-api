package fr.avenirsesr.portfolio.additionalskill.domain.service;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.port.input.AdditionalSkillService;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.AdditionalSkillCache;
import java.util.Optional;

import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class AdditionalSkillServiceImpl implements AdditionalSkillService {
  private final AdditionalSkillCache additionalSkillCache;

  @Override
  public PagedResult<AdditionalSkill> getAdditionalSkills(PageCriteria pageCriteria) {
    return additionalSkillCache.findAll(pageCriteria);
  }

  @Override
  public PagedResult<AdditionalSkill> searchAdditionalSkills(
      String keyword, PageCriteria pageCriteria) {
    return additionalSkillCache.findBySkillTitle(keyword, pageCriteria);
  }
}
