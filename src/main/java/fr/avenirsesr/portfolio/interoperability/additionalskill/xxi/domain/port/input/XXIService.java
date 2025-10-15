package fr.avenirsesr.portfolio.interoperability.additionalskill.xxi.domain.port.input;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import java.util.List;

public interface XXIService {
  List<AdditionalSkill> syncSkills();
}
