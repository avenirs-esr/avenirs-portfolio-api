package fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.model.mapper;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillCategoryType;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.model.Competence;

public interface CompetenceMapper {
  static AdditionalSkillCategory toCategoryDomain(Competence entity) {
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
            EAdditionalSkillCategoryType.ISSUE);
    AdditionalSkillCategory target =
        AdditionalSkillCategory.of(
            entity.getMacroCompetence().getObjectif().getLibelle(),
            issue,
            EAdditionalSkillCategoryType.TARGET);

    return AdditionalSkillCategory.of(
        entity.getMacroCompetence().getLibelle(), target, EAdditionalSkillCategoryType.MACRO_SKILL);
  }
}
