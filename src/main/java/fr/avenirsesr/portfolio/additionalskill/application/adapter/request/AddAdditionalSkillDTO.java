package fr.avenirsesr.portfolio.additionalskill.application.adapter.request;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddAdditionalSkillDTO {
  String id;

  @Schema(ref = "#/components/schemas/EAdditionalSkillType")
  EAdditionalSkillType type;

  @Schema(ref = "#/components/schemas/EAdditionalSkillLevel")
  EAdditionalSkillLevel level;
}
