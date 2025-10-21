package fr.avenirsesr.portfolio.trace.domain.data;

import java.util.List;

public record TraceAssociationsData(
    List<SkillLevelAssociationData> skillLevelAssociations,
    List<AdditionalSkillAssociationData> additionalSkillAssociations) {}
