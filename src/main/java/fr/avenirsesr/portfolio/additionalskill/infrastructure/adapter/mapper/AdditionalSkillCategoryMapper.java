package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillCategoryType;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillCategoryEntity;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.model.Competence;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.model.CompetenceComplementaireDetaillee;

public interface AdditionalSkillCategoryMapper {
  static AdditionalSkillCategory toDomain(CompetenceComplementaireDetaillee entity) {

    AdditionalSkillCategory domain =
        AdditionalSkillCategory.of(
            entity.macroCompetence().objectif().enjeu().domaineCompetence().libelle(),
            null,
            EAdditionalSkillCategoryType.DOMAIN);
    AdditionalSkillCategory issue =
        AdditionalSkillCategory.of(
            entity.macroCompetence().objectif().enjeu().libelle(),
            domain,
            EAdditionalSkillCategoryType.DOMAIN);
    AdditionalSkillCategory target =
        AdditionalSkillCategory.of(
            entity.macroCompetence().objectif().libelle(),
            issue,
            EAdditionalSkillCategoryType.DOMAIN);

    return AdditionalSkillCategory.of(
        entity.macroCompetence().libelle(), target, EAdditionalSkillCategoryType.DOMAIN);
  }

  static AdditionalSkillCategory toDomain(Competence entity) {

    AdditionalSkillCategory domain =
        AdditionalSkillCategory.of(
            entity
                .getMacroCompetence()
                .getObjectif()
                .getEnjeu()
                .getDomaineCompetence()
                .getLibelle(),
            null,
            EAdditionalSkillCategoryType.DOMAIN);
    AdditionalSkillCategory issue =
        AdditionalSkillCategory.of(
            entity.getMacroCompetence().getObjectif().getEnjeu().getLibelle(),
            domain,
            EAdditionalSkillCategoryType.DOMAIN);
    AdditionalSkillCategory target =
        AdditionalSkillCategory.of(
            entity.getMacroCompetence().getObjectif().getLibelle(),
            issue,
            EAdditionalSkillCategoryType.DOMAIN);

    return AdditionalSkillCategory.of(
        entity.getMacroCompetence().getLibelle(), target, EAdditionalSkillCategoryType.DOMAIN);
  }

  static AdditionalSkillCategoryEntity fromDomain(AdditionalSkillCategory domain) {
    return AdditionalSkillCategoryEntity.of(
        domain.getId(),
        domain.getLibelle(),
        domain.getType(),
        domain.getParent().map(AdditionalSkillCategoryMapper::fromDomain).orElse(null));
  }
}
