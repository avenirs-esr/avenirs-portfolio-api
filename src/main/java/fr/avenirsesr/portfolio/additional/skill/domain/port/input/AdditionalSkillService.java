package fr.avenirsesr.portfolio.additional.skill.domain.port.input;

import fr.avenirsesr.portfolio.additional.skill.domain.model.AdditionalSkill;
import java.util.List;

public interface AdditionalSkillService {
  List<AdditionalSkill> getAdditionalSkills();
}
