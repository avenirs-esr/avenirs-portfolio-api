package fr.avenirsesr.portfolio.student.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.student.experience.application.adapter.dto.DeclaredExperienceViewDTO;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.DeclaredSkillProgressDetailsDTO;
import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.TraceDetailDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(requiredProperties = {"traces", "declaredSkills", "declaredExperiences"})
public record FeedbackAssociationsDTO(
    List<TraceDetailDTO> traces,
    List<DeclaredSkillProgressDetailsDTO> declaredSkills,
    List<DeclaredExperienceViewDTO> declaredExperiences) {}
