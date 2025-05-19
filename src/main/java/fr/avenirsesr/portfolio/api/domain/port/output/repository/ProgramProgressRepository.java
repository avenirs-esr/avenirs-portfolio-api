package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import java.util.List;

public interface ProgramProgressRepository {
  void save(ProgramProgress progress);

  void saveAll(List<ProgramProgress> progress);
}
