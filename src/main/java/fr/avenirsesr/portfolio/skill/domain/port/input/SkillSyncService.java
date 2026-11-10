package fr.avenirsesr.portfolio.skill.domain.port.input;

import fr.avenirsesr.portfolio.common.skill.application.adapter.dto.SkillCatalogDTO;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import java.util.List;

public interface SkillSyncService {
  List<ESkillReferentialType> syncNationalCatalogs();

  boolean syncNationalCatalog(ESkillReferentialType type);

  List<Skill> applyCatalog(SkillCatalogDTO catalog);
}
