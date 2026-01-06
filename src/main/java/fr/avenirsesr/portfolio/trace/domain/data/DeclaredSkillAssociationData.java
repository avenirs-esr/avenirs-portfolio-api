package fr.avenirsesr.portfolio.trace.domain.data;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.declaredskill.domain.model.enums.EDeclaredSkillLevel;
import java.util.List;
import java.util.UUID;

public record DeclaredSkillAssociationData(
    UUID id,
    String title,
    EDeclaredSkillLevel level,
    List<String> pathSegments,
    EExternalSkillType type) {}
