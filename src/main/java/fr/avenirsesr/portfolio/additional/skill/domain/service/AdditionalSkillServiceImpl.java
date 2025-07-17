package fr.avenirsesr.portfolio.additional.skill.domain.service;

import fr.avenirsesr.portfolio.additional.skill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additional.skill.domain.port.input.AdditionalSkillService;
import fr.avenirsesr.portfolio.additional.skill.domain.port.output.AdditionalSkillRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class AdditionalSkillServiceImpl implements AdditionalSkillService {

  private final AdditionalSkillRepository additionalSkillRepository;

  @Override
  public List<AdditionalSkill> getAdditionalSkills() {
    return additionalSkillRepository.findAll();
  }
}
