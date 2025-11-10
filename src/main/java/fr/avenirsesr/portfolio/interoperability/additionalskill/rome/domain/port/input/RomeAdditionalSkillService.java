package fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.port.input;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import java.util.List;

public interface RomeAdditionalSkillService {

  List<AdditionalSkill> synchronizeAndSaveAdditionalSkills(List<AdditionalSkill> additionalSkill);

  boolean checkRomeVersionUpdated();
}
