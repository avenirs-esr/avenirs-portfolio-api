package fr.avenirsesr.portfolio.staff.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.shared.application.adapter.mapper.OptionalMapper;
import fr.avenirsesr.portfolio.staff.activity.application.adapter.dto.ActivityContentDTO;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityDraft;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OptionalMapper.class)
public interface ActivityContentDtoMapper {

  @Mapping(target = "hasEnrolledStudent", expression = "java(null)")
  @Mapping(target = "files", source = "files")
  ActivityContentDTO toDTO(Activity activity, List<FileDTO> files);

  @Mapping(target = "hasEnrolledStudent", expression = "java(null)")
  ActivityContentDTO toDTO(Activity activity);

  @Mapping(target = "hasEnrolledStudent", source = "hasEnrolledStudent")
  @Mapping(target = "files", source = "files")
  ActivityContentDTO toDTO(ActivityDraft activity, Boolean hasEnrolledStudent, List<FileDTO> files);
}
