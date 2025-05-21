package fr.avenirsesr.portfolio.api.domain.port.input;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import java.util.List;
import java.util.UUID;

public interface ProgramProgressService {
  List<ProgramProgress> getSkillsOverview(UUID userId);
}
