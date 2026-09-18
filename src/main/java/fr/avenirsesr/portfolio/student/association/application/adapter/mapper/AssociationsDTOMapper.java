package fr.avenirsesr.portfolio.student.association.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.activity.application.adapter.mapper.DeclaredActivityAssociationMapper;
import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationsDTO;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociatedElementsData;
import fr.avenirsesr.portfolio.student.experience.application.adapter.mapper.DeclaredExperienceMapper;
import fr.avenirsesr.portfolio.student.program.application.adapter.mapper.DeclaredProgramViewMapper;
import fr.avenirsesr.portfolio.student.skill.application.adapter.mapper.DeclaredSkillProgressMapper;
import fr.avenirsesr.portfolio.student.trace.application.adapter.mapper.TraceOverviewMapper;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {
      TraceOverviewMapper.class,
      DeclaredActivityAssociationMapper.class,
      DeclaredSkillProgressMapper.class,
      DeclaredExperienceMapper.class,
      DeclaredProgramViewMapper.class
    })
public interface AssociationsDTOMapper {

  AssociationsDTO toDTO(AssociatedElementsData associatedElements);
}
