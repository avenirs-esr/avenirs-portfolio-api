package fr.avenirsesr.portfolio.skill.domain.port.output.client;

import fr.avenirsesr.portfolio.common.skill.application.adapter.dto.SkillCatalogDTO;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import java.util.Optional;

public interface SkillCatalogClient {
  Optional<SkillCatalogDTO> fetchNationalCatalog(ESkillReferentialType type);
}
