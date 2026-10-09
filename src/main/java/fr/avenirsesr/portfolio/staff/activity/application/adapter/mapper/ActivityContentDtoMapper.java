package fr.avenirsesr.portfolio.staff.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityContentDTO;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.AuthorDTO;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityDraft;
import java.util.List;

import fr.avenirsesr.portfolio.user.domain.model.Staff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public interface ActivityContentDtoMapper {
  @Mapping(target = "haveDraft", expression = "java(false)")
  @Mapping(target = "hasEnrolledStudent", expression = "java(null)")
  @Mapping(target = "files", source = "files")
  ActivityContentDTO toDTO(Activity activity, List<FileDTO> files);

  @Mapping(target = "haveDraft", expression = "java(false)")
  @Mapping(target = "hasEnrolledStudent", expression = "java(null)")
  ActivityContentDTO toDTO(Activity activity);

  @Mapping(target = "haveDraft", expression = "java(false)")
  @Mapping(target = "hasEnrolledStudent", source = "hasEnrolledStudent")
  @Mapping(target = "files", source = "files")
  ActivityContentDTO toDTO(ActivityDraft activity, Boolean hasEnrolledStudent, List<FileDTO> files);

  @Mapping(target = "haveDraft", source = "haveDraft")
  @Mapping(target = "hasEnrolledStudent", expression = "java(null)")
  @Mapping(target = "files", source = "files")
  ActivityContentDTO toDTO(Activity activity, Boolean haveDraft, List<FileDTO> files);

  @Mapping(target = "firstName", source = "user.firstName")
  @Mapping(target = "lastName", source = "user.lastName")
  @Mapping(target = "userId", source = "user.id")
  AuthorDTO toAuthorDTOto(Staff staff);
}
