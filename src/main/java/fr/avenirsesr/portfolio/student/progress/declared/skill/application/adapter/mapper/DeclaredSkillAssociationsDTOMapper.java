package fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityViewDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto.DeclaredSkillAssociationsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillAssociationsData;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceOverviewMapper;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {TraceOverviewMapper.class, DeclaredActivityViewDTOMapper.class})
public interface DeclaredSkillAssociationsDTOMapper {

  DeclaredSkillAssociationsDTO toDTO(DeclaredSkillAssociationsData associations);
}
