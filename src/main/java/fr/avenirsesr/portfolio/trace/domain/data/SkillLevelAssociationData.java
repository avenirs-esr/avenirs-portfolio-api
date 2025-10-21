package fr.avenirsesr.portfolio.trace.domain.data;

import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import java.util.UUID;

public record SkillLevelAssociationData(
    UUID id, String skillTitle, String level, ESkillLevelStatus status, AmsAssociationData ams) {}
