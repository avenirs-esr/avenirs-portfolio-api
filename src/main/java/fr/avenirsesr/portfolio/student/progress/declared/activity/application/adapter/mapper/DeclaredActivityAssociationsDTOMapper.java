package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityAssociationsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityAssociationsData;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.mapper.DeclaredSkillProgressMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceOverviewMapper;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {TraceOverviewMapper.class, DeclaredSkillProgressMapper.class})
public interface DeclaredActivityAssociationsDTOMapper {

  DeclaredActivityAssociationsDTO toDTO(DeclaredActivityAssociationsData associations);
}
