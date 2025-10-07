package fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.port.output;

import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.model.Competence;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.model.Rome4Version;
import java.util.List;

public interface RomeAdditionalSkillApi {
  Rome4Version fetchRomeVersion();

  List<Competence> fetchAdditionalSkills();
}
