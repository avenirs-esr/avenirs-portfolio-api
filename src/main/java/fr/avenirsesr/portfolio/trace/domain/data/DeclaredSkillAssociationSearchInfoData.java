package fr.avenirsesr.portfolio.trace.domain.data;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import java.util.UUID;

public record DeclaredSkillAssociationSearchInfoData(
    UUID id, String title, EExternalSkillType type, boolean disabled) {}
