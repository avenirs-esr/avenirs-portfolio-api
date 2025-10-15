package fr.avenirsesr.portfolio.interoperability.additionalskill.casoc.domain.port.input;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import java.util.List;

public interface CasocService {
  List<AdditionalSkill> syncSkills();
}
