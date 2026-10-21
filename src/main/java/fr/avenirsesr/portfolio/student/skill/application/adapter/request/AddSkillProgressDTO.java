package fr.avenirsesr.portfolio.student.skill.application.adapter.request;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_TEXT_LENGTH;

import fr.avenirsesr.portfolio.student.skill.domain.model.enums.ESkillLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddSkillProgressDTO {
  String id;

  @Schema(ref = "#/components/schemas/ESkillLevel")
  ESkillLevel level;

  @Size(
      max = RICH_TEXT_LENGTH,
      message = "The reflection cannot exceed " + RICH_TEXT_LENGTH + " characters")
  String reflection;
}
