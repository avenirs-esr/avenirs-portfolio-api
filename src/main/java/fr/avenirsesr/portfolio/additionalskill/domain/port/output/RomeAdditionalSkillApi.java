package fr.avenirsesr.portfolio.additionalskill.domain.port.output;

import fr.avenirsesr.portfolio.additionalskill.domain.model.Rome4Version;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.Competence;
import java.util.List;

public interface RomeAdditionalSkillApi {
  Rome4Version fetchRomeVersion();

  List<Competence> fetchAdditionalSkills();
}
