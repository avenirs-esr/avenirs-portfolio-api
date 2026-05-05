package fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.mapper;

import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillCategoryDTO;
import fr.avenirsesr.portfolio.declaredskill.application.adapter.dto.DeclaredSkillCategoryDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto.DeclaredSkillProgressDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto.DeclaredSkillProgressDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillProgressDetails;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceOverviewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = TraceOverviewMapper.class)
public interface DeclaredSkillProgressMapper {

  @Mapping(source = "skill.libelle", target = "title")
  @Mapping(source = "skill.pathSegments", target = "pathSegments")
  @Mapping(source = "skill.type", target = "type")
  DeclaredSkillProgressDTO toDeclaredSkillProgressDTO(DeclaredSkillProgress declaredSkillProgress);

  @Mapping(source = "declaredSkillProgress.id", target = "id")
  @Mapping(source = "declaredSkillProgress.skill.libelle", target = "title")
  @Mapping(source = "externalCategories", target = "pathSegments")
  @Mapping(source = "declaredSkillProgress.reflection", target = "reflection")
  @Mapping(source = "declaredSkillProgress.skill.type", target = "type")
  @Mapping(source = "declaredSkillProgress.level", target = "level")
  @Mapping(source = "declaredSkillProgress.createdAt", target = "createdAt")
  @Mapping(source = "declaredSkillProgress.updatedAt", target = "updatedAt")
  DeclaredSkillProgressDetailsDTO toDeclaredSkillProgressDetailsDTO(
      DeclaredSkillProgressDetails declaredSkillProgressDetails);

  DeclaredSkillCategoryDTO toCategoryDTO(ExternalSkillCategoryDTO externalCategory);
}
