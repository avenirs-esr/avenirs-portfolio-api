package fr.avenirsesr.portfolio.skill.domain.port.input;

import fr.avenirsesr.portfolio.skill.domain.data.SkillPathSegment;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SkillService {
  Skill getWithAncestorsById(UUID id);

  List<Skill> getAllWithAncestorsById(List<UUID> ids);

  List<SkillPathSegment> getDetailedPath(Skill skill);

  Map<UUID, List<SkillPathSegment>> getDetailedPaths(List<Skill> skills);
}
