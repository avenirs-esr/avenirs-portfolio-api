package fr.avenirsesr.portfolio.skill.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.common.skill.application.adapter.dto.SkillCatalogDTO;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.skill.domain.port.input.SkillSyncService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class TransactionalSkillSyncService implements SkillSyncService {
  private final SkillSyncService delegate;

  @Override
  @Transactional
  public List<ESkillReferentialType> syncNationalCatalogs() {
    return delegate.syncNationalCatalogs();
  }

  @Override
  @Transactional
  public boolean syncNationalCatalog(ESkillReferentialType type) {
    return delegate.syncNationalCatalog(type);
  }

  @Override
  @Transactional
  public List<Skill> applyCatalog(SkillCatalogDTO catalog) {
    return delegate.applyCatalog(catalog);
  }
}
