package fr.avenirsesr.portfolio.student.skill.application.adapter.request;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_TEXT_LENGTH;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.declaredskill.domain.model.enums.EDeclaredSkillLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddDeclaredSkillDTO {
  String id;

  @Schema(ref = "#/components/schemas/EExternalSkillType")
  EExternalSkillType type;

  @Schema(ref = "#/components/schemas/EDeclaredSkillLevel")
  EDeclaredSkillLevel level;

  @Size(
      max = RICH_TEXT_LENGTH,
      message = "The reflection cannot exceed " + RICH_TEXT_LENGTH + " characters")
  String reflection;
}
