package fr.avenirsesr.portfolio.staff.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityContentDTO;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public interface ActivityContentDtoMapper {
  @Mapping(target = "activityStatus", source = "activity.status")
  @Mapping(target = "haveDraft", expression = "java(false)")
  @Mapping(target = "hasEnrolledStudent", expression = "java(null)")
  @Mapping(target = "rights", expression = "java(java.util.List.of())")
  @Mapping(target = "files", source = "files")
  ActivityContentDTO toDTO(Activity activity, List<FileDTO> files);

  @Mapping(target = "activityStatus", source = "activity.status")
  @Mapping(target = "haveDraft", expression = "java(false)")
  @Mapping(target = "hasEnrolledStudent", expression = "java(null)")
  @Mapping(target = "rights", expression = "java(java.util.List.of())")
  ActivityContentDTO toDTO(Activity activity);

  @Mapping(target = "activityStatus", expression = "java(EActivityStatus.DRAFT)")
  @Mapping(target = "haveDraft", expression = "java(false)")
  @Mapping(target = "hasEnrolledStudent", source = "hasEnrolledStudent")
  @Mapping(target = "rights", source = "rights")
  @Mapping(target = "files", source = "files")
  ActivityContentDTO toDTO(
      ActivityDraft activity,
      Boolean hasEnrolledStudent,
      Set<EActivityRight> rights,
      List<FileDTO> files);

  @Mapping(target = "activityStatus", source = "activity.status")
  @Mapping(target = "haveDraft", source = "haveDraft")
  @Mapping(target = "rights", source = "rights")
  @Mapping(target = "hasEnrolledStudent", expression = "java(null)")
  @Mapping(target = "files", source = "files")
  ActivityContentDTO toDTO(
      Activity activity, Boolean haveDraft, Set<EActivityRight> rights, List<FileDTO> files);
}
