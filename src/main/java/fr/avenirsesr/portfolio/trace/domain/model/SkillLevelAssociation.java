package fr.avenirsesr.portfolio.trace.domain.model;

import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import java.util.UUID;

public record SkillLevelAssociation(
    UUID id, String title, String level, ESkillLevelStatus status, AmsAssociation ams) {}
