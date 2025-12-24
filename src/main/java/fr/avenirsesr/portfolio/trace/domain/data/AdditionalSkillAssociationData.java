package fr.avenirsesr.portfolio.trace.domain.data;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import java.util.List;
import java.util.UUID;

public record AdditionalSkillAssociationData(
    UUID id,
    String title,
    EAdditionalSkillLevel level,
    List<String> pathSegments,
    EExternalSkillType type) {}
