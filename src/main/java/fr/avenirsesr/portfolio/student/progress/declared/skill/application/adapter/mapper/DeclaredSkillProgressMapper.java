package fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.mapper;

import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillCategoryDTO;
import fr.avenirsesr.portfolio.declaredskill.application.adapter.dto.DeclaredSkillCategoryDTO;
import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto.DeclaredSkillAssociationCountDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto.DeclaredSkillProgressDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto.DeclaredSkillProgressDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillAssociationCount;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillProgressData;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillProgressDetails;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.trace.application.adapter.mapper.TraceOverviewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {TraceOverviewMapper.class, OptionalMapper.class})
public interface DeclaredSkillProgressMapper {

  @Mapping(source = "skill.libelle", target = "title")
  @Mapping(source = "skill.pathSegments", target = "pathSegments")
  @Mapping(source = "skill.type", target = "type")
  DeclaredSkillProgressDTO toDeclaredSkillProgressDTO(DeclaredSkillProgress declaredSkillProgress);

  @Mapping(source = "declaredSkillProgress.id", target = "id")
  @Mapping(source = "declaredSkillProgress.skill.libelle", target = "title")
  @Mapping(source = "declaredSkillProgress.skill.pathSegments", target = "pathSegments")
  @Mapping(source = "declaredSkillProgress.skill.type", target = "type")
  @Mapping(source = "declaredSkillProgress.level", target = "level")
  @Mapping(source = "declaredSkillProgress.reflection", target = "reflection")
  @Mapping(source = "declaredSkillProgress.valorized", target = "valorized")
  @Mapping(source = "associationsCount", target = "associationsCount")
  DeclaredSkillProgressDTO toDeclaredSkillProgressDTO(
      DeclaredSkillProgressData declaredSkillProgressData);

  DeclaredSkillAssociationCountDTO toAssociationCountDTO(
      DeclaredSkillAssociationCount associationsCount);

  @Mapping(source = "declaredSkillProgress.id", target = "id")
  @Mapping(source = "declaredSkillProgress.skill.libelle", target = "title")
  @Mapping(source = "externalCategories", target = "pathSegments")
  @Mapping(source = "declaredSkillProgress.reflection", target = "reflection")
  @Mapping(source = "declaredSkillProgress.skill.type", target = "type")
  @Mapping(source = "declaredSkillProgress.level", target = "level")
  @Mapping(source = "declaredSkillProgress.createdAt", target = "createdAt")
  @Mapping(source = "declaredSkillProgress.updatedAt", target = "updatedAt")
  @Mapping(source = "declaredSkillProgress.valorized", target = "valorized")
  DeclaredSkillProgressDetailsDTO toDeclaredSkillProgressDetailsDTO(
      DeclaredSkillProgressDetails declaredSkillProgressDetails);

  DeclaredSkillCategoryDTO toCategoryDTO(ExternalSkillCategoryDTO externalCategory);
}
