package fr.avenirsesr.portfolio.student.skill.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.activity.application.adapter.mapper.DeclaredActivityAssociationMapper;
import fr.avenirsesr.portfolio.student.experience.application.adapter.mapper.DeclaredExperienceMapper;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.DeclaredSkillAssociationsDTO;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillAssociationsData;
import fr.avenirsesr.portfolio.student.trace.application.adapter.mapper.TraceOverviewMapper;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {
      TraceOverviewMapper.class,
      DeclaredActivityAssociationMapper.class,
      DeclaredExperienceMapper.class
    })
public interface DeclaredSkillAssociationsDTOMapper {

  DeclaredSkillAssociationsDTO toDTO(DeclaredSkillAssociationsData associations);
}
