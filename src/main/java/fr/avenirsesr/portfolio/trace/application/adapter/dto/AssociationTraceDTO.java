package fr.avenirsesr.portfolio.trace.application.adapter.dto;

public record AssociationTraceDTO(
    SkillLevelAssociationDTO skillLevelAssociation,
    AdditionalSkillAssociationDTO additionalSkillAssociation) {
  public AssociationTraceDTO {
    if (skillLevelAssociation == null && additionalSkillAssociation == null) {
      throw new IllegalArgumentException(
          "At least the skill level or additional skill must be provided.");
    }
  }
}
