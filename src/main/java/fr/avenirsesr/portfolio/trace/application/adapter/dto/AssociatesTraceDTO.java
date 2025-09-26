package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.additionalskill.application.adapter.dto.AdditionalSkillProgressDTO;
import fr.avenirsesr.portfolio.ams.application.adapter.dto.AmsViewDTO;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(requiredProperties = {"amses", "skills", "additionalSkillProgresses"})
public record AssociatesTraceDTO(
    List<AmsViewDTO> amses,
    List<SkillDTO> skills,
    List<AdditionalSkillProgressDTO> additionalSkillProgresses) {}
