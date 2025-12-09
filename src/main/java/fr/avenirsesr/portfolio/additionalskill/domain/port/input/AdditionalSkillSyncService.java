package fr.avenirsesr.portfolio.additionalskill.domain.port.input;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import java.util.Optional;
import java.util.UUID;

public interface AdditionalSkillSyncService {
  Optional<AdditionalSkill> getOrCreateFromExternalSkill(UUID externalSkillId);
}
