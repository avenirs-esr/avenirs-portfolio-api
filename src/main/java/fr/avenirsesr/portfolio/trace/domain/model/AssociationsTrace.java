package fr.avenirsesr.portfolio.trace.domain.model;

import java.util.List;

public record AssociationsTrace(
    List<SkillLevelAssociation> skillLevelAssociations,
    List<AdditionalSkillAssociation> additionalSkillAssociations) {}
