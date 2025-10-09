package fr.avenirsesr.portfolio.interoperability.additionalskill.casoc.domain.service;

import fr.avenirsesr.portfolio.interoperability.additionalskill.casoc.domain.model.Competence;
import java.util.List;

public interface CompetenceReader {
  List<Competence> readCompetences();
}
