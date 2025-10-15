package fr.avenirsesr.portfolio.interoperability.additionalskill.casol.domain.port.input;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import java.util.List;

public interface CasolService {
  List<AdditionalSkill> syncSkills();
}
