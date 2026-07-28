package fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.dto.DeclaredExperienceAssociationsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.dto.DeclaredExperienceViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.data.DeclaredExperienceAssociationsData;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.mapper.DeclaredSkillProgressMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceOverviewMapper;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {TraceOverviewMapper.class, DeclaredSkillProgressMapper.class})
public interface DeclaredExperienceMapper {

  DeclaredExperienceViewDTO toDTO(DeclaredExperience experience);

  DeclaredExperienceAssociationsDTO toAssociationsDTO(
      DeclaredExperienceAssociationsData declaredExperienceAssociations);
}
