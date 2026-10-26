package fr.avenirsesr.portfolio.student.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.student.experience.application.adapter.dto.DeclaredExperienceViewDTO;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.SkillProgressDetailsDTO;
import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.TraceDetailDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(requiredProperties = {"traces", "skillProgresses", "declaredExperiences"})
public record DeclaredActivityAssociationsDTO(
    List<TraceDetailDTO> traces,
    List<SkillProgressDetailsDTO> skillProgresses,
    List<DeclaredExperienceViewDTO> declaredExperiences) {}
