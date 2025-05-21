package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import java.util.List;
import java.util.UUID;

public interface ProgramProgressRepository {
  void save(ProgramProgress progress);

  void saveAll(List<ProgramProgress> progress);

  List<ProgramProgress> getSkillsOverview(UUID userId);
}
