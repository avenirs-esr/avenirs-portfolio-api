package fr.avenirsesr.portfolio.interoperability.additionalskill.xxi.domain.model;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillCategoryType;

public record Category(
    int id, String libelle, Category parent, EAdditionalSkillCategoryType type) {}
