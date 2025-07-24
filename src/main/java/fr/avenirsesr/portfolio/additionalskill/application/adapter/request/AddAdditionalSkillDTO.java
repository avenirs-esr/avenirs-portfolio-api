package fr.avenirsesr.portfolio.additionalskill.application.adapter.request;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddAdditionalSkillDTO {
  String id;
  EAdditionalSkillType type;
  EAdditionalSkillLevel level;
}
