package fr.avenirsesr.portfolio.trace.domain.model;

import java.util.List;

public record TraceAssociations(
    List<SkillLevelAssociation> skillLevelAssociations,
    List<AdditionalSkillAssociation> additionalSkillAssociations) {}
