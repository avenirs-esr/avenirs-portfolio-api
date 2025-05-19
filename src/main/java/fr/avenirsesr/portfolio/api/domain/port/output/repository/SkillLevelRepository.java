package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import java.util.List;

public interface SkillLevelRepository {
  void save(SkillLevel skillLevel);

  void saveAll(List<SkillLevel> skillLevels);
}
