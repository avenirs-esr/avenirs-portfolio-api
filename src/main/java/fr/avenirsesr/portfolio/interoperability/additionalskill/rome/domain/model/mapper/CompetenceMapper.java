package fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.model.mapper;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillCategoryType;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.model.Competence;
import java.util.List;

public class CompetenceMapper {
  public static AdditionalSkillCategory toCategoryDomain(
      Competence entity, List<AdditionalSkillCategory> categories) {
    AdditionalSkillCategory domain =
        fetchCategory(entity, EAdditionalSkillCategoryType.DOMAIN, null, categories);
    AdditionalSkillCategory issue =
        fetchCategory(entity, EAdditionalSkillCategoryType.ISSUE, domain, categories);
    AdditionalSkillCategory target =
        fetchCategory(entity, EAdditionalSkillCategoryType.TARGET, issue, categories);

    return fetchCategory(entity, EAdditionalSkillCategoryType.MACRO_SKILL, target, categories);
  }

  private static AdditionalSkillCategory fetchCategory(
      Competence entity,
      EAdditionalSkillCategoryType type,
      AdditionalSkillCategory parent,
      List<AdditionalSkillCategory> categories) {

    var category = AdditionalSkillCategory.of(labelOf(entity, type), parent, type);
    var optionalCategory =
        categories.stream().filter(c -> c.uniqHash() == category.uniqHash()).findFirst();

    if (optionalCategory.isPresent()) {
      return optionalCategory.get();
    } else {
      categories.add(category);
      return category;
    }
  }

  private static String labelOf(Competence entity, EAdditionalSkillCategoryType type) {
    return switch (type) {
      case DOMAIN ->
          entity.getMacroCompetence().getObjectif().getEnjeu().getDomaineCompetence().getLibelle();
      case ISSUE -> entity.getMacroCompetence().getObjectif().getEnjeu().getLibelle();
      case TARGET -> entity.getMacroCompetence().getObjectif().getLibelle();
      case MACRO_SKILL -> entity.getMacroCompetence().getLibelle();
    };
  }
}
