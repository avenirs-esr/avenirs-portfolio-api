package fr.avenirsesr.portfolio.student.experience.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.experience.application.adapter.dto.DeclaredExperienceAssociationCountDTO;
import fr.avenirsesr.portfolio.student.experience.application.adapter.dto.DeclaredExperienceAssociationsDTO;
import fr.avenirsesr.portfolio.student.experience.application.adapter.dto.DeclaredExperienceViewDTO;
import fr.avenirsesr.portfolio.student.experience.domain.data.DeclaredExperienceAssociationCount;
import fr.avenirsesr.portfolio.student.experience.domain.data.DeclaredExperienceAssociationsData;
import fr.avenirsesr.portfolio.student.experience.domain.data.DeclaredExperienceData;
import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.skill.application.adapter.mapper.DeclaredSkillProgressMapper;
import fr.avenirsesr.portfolio.student.trace.application.adapter.mapper.TraceOverviewMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {TraceOverviewMapper.class, DeclaredSkillProgressMapper.class})
public interface DeclaredExperienceMapper {

  DeclaredExperienceViewDTO toDTO(DeclaredExperience experience);

  @Mapping(source = "declaredExperience.id", target = "id")
  @Mapping(source = "declaredExperience.title", target = "title")
  @Mapping(source = "declaredExperience.experienceType", target = "experienceType")
  @Mapping(source = "declaredExperience.organization", target = "organization")
  @Mapping(source = "declaredExperience.activitySector", target = "activitySector")
  @Mapping(source = "declaredExperience.location", target = "location")
  @Mapping(source = "declaredExperience.description", target = "description")
  @Mapping(source = "declaredExperience.sourceOfInformation", target = "sourceOfInformation")
  @Mapping(source = "declaredExperience.summary", target = "summary")
  @Mapping(source = "declaredExperience.externalLink", target = "externalLink")
  @Mapping(source = "declaredExperience.result", target = "result")
  @Mapping(source = "declaredExperience.startDate", target = "startDate")
  @Mapping(source = "declaredExperience.endDate", target = "endDate")
  @Mapping(source = "declaredExperience.valorized", target = "valorized")
  @Mapping(source = "declaredExperience.createdAt", target = "createdAt")
  @Mapping(source = "declaredExperience.updatedAt", target = "updatedAt")
  @Mapping(source = "associationsCount", target = "declaredExperienceAssociationCountDTO")
  DeclaredExperienceViewDTO toDTO(DeclaredExperienceData declaredExperienceData);

  DeclaredExperienceAssociationCountDTO toAssociationCountDTO(
      DeclaredExperienceAssociationCount associationsCount);

  DeclaredExperienceAssociationsDTO toAssociationsDTO(
      DeclaredExperienceAssociationsData declaredExperienceAssociations);
}
