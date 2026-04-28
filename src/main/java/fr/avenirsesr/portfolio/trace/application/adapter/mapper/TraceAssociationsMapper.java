package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityViewDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.mapper.DeclaredExperienceMapper;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.mapper.DeclaredSkillProgressMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceAssociationsDTO;
import fr.avenirsesr.portfolio.trace.domain.data.TraceAssociationsData;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {
      DeclaredActivityViewDTOMapper.class,
      DeclaredSkillProgressMapper.class,
      DeclaredExperienceMapper.class
    })
public interface TraceAssociationsMapper {

  TraceAssociationsDTO toDTO(TraceAssociationsData traceAssociations);
}
