package fr.avenirsesr.portfolio.student.skill.application.adapter.mapper;

import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import fr.avenirsesr.portfolio.skill.domain.data.SkillPathSegment;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.SkillPathSegmentDTO;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.SkillProgressAssociationCountDTO;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.SkillProgressDTO;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.SkillProgressDetailsDTO;
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressAssociationCount;
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressData;
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressDetails;
import fr.avenirsesr.portfolio.student.trace.application.adapter.mapper.TraceOverviewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {TraceOverviewMapper.class, OptionalMapper.class})
public interface SkillProgressMapper {

  @Mapping(source = "skillProgress.id", target = "id")
  @Mapping(source = "skill.id", target = "skillId")
  @Mapping(source = "skill.title", target = "title")
  @Mapping(source = "skill.pathSegments", target = "pathSegments")
  @Mapping(source = "skill.referential.type", target = "type")
  @Mapping(source = "skillProgress.level", target = "level")
  @Mapping(source = "skillProgress.reflection", target = "reflection")
  @Mapping(source = "skillProgress.valorized", target = "valorized")
  @Mapping(source = "associationsCount", target = "associationsCount")
  SkillProgressDTO toSkillProgressDTO(SkillProgressData skillProgressData);

  SkillProgressAssociationCountDTO toAssociationCountDTO(
      SkillProgressAssociationCount associationsCount);

  @Mapping(source = "skillProgress.id", target = "id")
  @Mapping(source = "skill.id", target = "skillId")
  @Mapping(source = "skill.title", target = "title")
  @Mapping(source = "skill.description", target = "description")
  @Mapping(source = "path", target = "pathSegments")
  @Mapping(source = "skillProgress.reflection", target = "reflection")
  @Mapping(source = "skill.referential.type", target = "type")
  @Mapping(source = "skillProgress.level", target = "level")
  @Mapping(source = "skillProgress.createdAt", target = "createdAt")
  @Mapping(source = "skillProgress.updatedAt", target = "updatedAt")
  @Mapping(source = "skillProgress.valorized", target = "valorized")
  SkillProgressDetailsDTO toSkillProgressDetailsDTO(SkillProgressDetails skillProgressDetails);

  @Mapping(source = "title", target = "libelle")
  @Mapping(source = "kind", target = "type")
  SkillPathSegmentDTO toCategoryDTO(SkillPathSegment pathSegment);
}
