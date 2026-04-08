package fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.request;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.REFLECTION_LENGTH;

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
      max = REFLECTION_LENGTH,
      message = "The reflection cannot exceed " + REFLECTION_LENGTH + " characters")
  String reflection;
}
