package fr.avenirsesr.portfolio.additionalskill.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EAdditionalSkillType {
  ROME4,
  ;

  EAdditionalSkillType() {}

  @JsonCreator
  public static EAdditionalSkillType fromValue(String value) {
    for (EAdditionalSkillType type : EAdditionalSkillType.values()) {
      if (type.name().equals(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
