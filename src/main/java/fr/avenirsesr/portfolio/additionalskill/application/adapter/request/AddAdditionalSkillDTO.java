package fr.avenirsesr.portfolio.additionalskill.application.adapter.request;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddAdditionalSkillDTO {
  String id;
  EAdditionalSkillType type;
  ESkillLevelStatus level;
}
