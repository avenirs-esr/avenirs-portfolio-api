package fr.avenirsesr.portfolio.student.progress.application.adapter.request;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
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

  @Size(max = 400, message = "The description cannot exceed 400 characters")
  String description;
}
