package fr.avenirsesr.portfolio.student.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityAssociationMapper;
import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.mapper.DeclaredExperienceMapper;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.mapper.DeclaredSkillProgressMapper;
import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.TraceAssociationsDTO;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceAssociationsData;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {
      DeclaredActivityAssociationMapper.class,
      DeclaredSkillProgressMapper.class,
      DeclaredExperienceMapper.class
    })
public interface TraceAssociationsMapper {

  TraceAssociationsDTO toDTO(TraceAssociationsData traceAssociations);
}
