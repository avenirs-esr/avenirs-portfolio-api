package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import fr.avenirsesr.portfolio.api.domain.model.Skill;
import java.util.List;

public interface SkillRepository {
  void save(Skill skill);

  void saveAll(List<Skill> skills);
}
