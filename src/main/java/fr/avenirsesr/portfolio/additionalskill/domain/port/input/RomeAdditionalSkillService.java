package fr.avenirsesr.portfolio.additionalskill.domain.port.input;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import java.util.List;

public interface RomeAdditionalSkillService {

  void cleanAndCreateAdditionalSkillIndex();

  List<AdditionalSkill> synchronizeAndIndexAdditionalSkills(List<AdditionalSkill> additionalSkill);

  boolean checkRomeVersionUpdated();
}
