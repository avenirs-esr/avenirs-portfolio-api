package fr.avenirsesr.portfolio.additionalskill.domain.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum EAdditionalSkillType {
  ROME4("ROME4.0"),
  ;

  private final String value;

  EAdditionalSkillType(String value) {
    this.value = value;
  }

  @JsonCreator
  public static EAdditionalSkillType fromValue(String value) {
    for (EAdditionalSkillType type : EAdditionalSkillType.values()) {
      if (type.getValue().equals(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
