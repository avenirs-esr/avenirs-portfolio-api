package fr.avenirsesr.portfolio.additional.skill.domain.port.output;

import fr.avenirsesr.portfolio.additional.skill.domain.model.AdditionalSkill;
import java.util.List;

public interface AdditionalSkillRepository {
  List<AdditionalSkill> findAll();
}
