package fr.avenirsesr.portfolio.student.skill.domain.port.input;

import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkill;
import java.util.Optional;
import java.util.UUID;

public interface DeclaredSkillSyncService {
  Optional<DeclaredSkill> getOrCreateFromExternalSkill(UUID id);
}
