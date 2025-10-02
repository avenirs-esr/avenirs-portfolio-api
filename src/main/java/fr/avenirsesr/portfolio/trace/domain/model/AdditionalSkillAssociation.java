package fr.avenirsesr.portfolio.trace.domain.model;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import java.util.List;
import java.util.UUID;

public record AdditionalSkillAssociation(
    UUID id,
    String title,
    EAdditionalSkillLevel level,
    List<String> pathSegments,
    EAdditionalSkillType type) {}
