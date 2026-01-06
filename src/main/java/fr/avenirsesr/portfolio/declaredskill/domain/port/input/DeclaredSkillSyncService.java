package fr.avenirsesr.portfolio.declaredskill.domain.port.input;

import fr.avenirsesr.portfolio.declaredskill.domain.model.DeclaredSkill;
import java.util.Optional;
import java.util.UUID;

public interface DeclaredSkillSyncService {
  Optional<DeclaredSkill> getOrCreateFromExternalSkill(UUID externalSkillId);
}
