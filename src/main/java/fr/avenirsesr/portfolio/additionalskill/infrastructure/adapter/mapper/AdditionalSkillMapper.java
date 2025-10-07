package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.*;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.model.Competence;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.model.CompetenceComplementaireDetaillee;
import java.time.Instant;

public interface AdditionalSkillMapper {
  static AdditionalSkill toDomain(CompetenceComplementaireDetaillee entity) {
    return AdditionalSkill.toDomain(
        entity.id(),
        entity.libelle(),
        entity.code(),
        AdditionalSkillCategoryMapper.toDomain(entity),
        EAdditionalSkillType.valueOf(entity.type()),
        Instant.now(),
        Instant.now());
  }

  static AdditionalSkill toDomain(AdditionalSkillEntity entity) {
    return AdditionalSkill.toDomain(
        entity.getId(),
        entity.getLibelle(),
        entity.getExternalId(),
        entity.getAdditionalSkillCategory().map(AdditionalSkillMapper::toDomain).orElse(null),
        entity.getType(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  static AdditionalSkillCategory toDomain(AdditionalSkillCategoryEntity entity) {
    return AdditionalSkillCategory.toDomain(
        entity.getId(),
        entity.getLibelle(),
        entity.getParent().map(AdditionalSkillMapper::toDomain).orElse(null),
        entity.getType(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  static AdditionalSkill createToDomain(Competence competence) {
    return AdditionalSkill.create(
        competence.getCode(),
        competence.getLibelle(),
        AdditionalSkillCategoryMapper.toDomain(competence),
        EAdditionalSkillType.ROME4);
  }

  static AdditionalSkillEntity fromDomain(AdditionalSkill domain) {
    return AdditionalSkillEntity.of(
        domain.getId(),
        domain.getExternalId(),
        domain.getLibelle(),
        domain.getType(),
        domain
            .getAdditionalSkillCategory()
            .map(AdditionalSkillCategoryMapper::fromDomain)
            .orElse(null));
  }
}
