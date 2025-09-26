package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.application.adapter.mapper.AdditionalSkillProgressMapper;
import fr.avenirsesr.portfolio.ams.application.adapter.mapper.AmsViewMapper;
import fr.avenirsesr.portfolio.student.progress.application.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.AssociatesTraceDTO;
import fr.avenirsesr.portfolio.trace.domain.model.AssociatesTrace;

public interface AssociatesTraceMapper {
  static AssociatesTraceDTO toDTO(AssociatesTrace associatesTrace) {
    return new AssociatesTraceDTO(
        associatesTrace.amses().stream().map(AmsViewMapper::toDto).toList(),
        associatesTrace.skillProgresses().stream()
            .map(
                skillProgress ->
                    SkillMapper.fromDomainToDto(
                        skillProgress.currentSkillLevelProgress(), skillProgress.studentProgress()))
            .toList(),
        associatesTrace.additionalSkillProgresses().stream()
            .map(AdditionalSkillProgressMapper::toAdditionalSkillProgressDTO)
            .toList());
  }
}
