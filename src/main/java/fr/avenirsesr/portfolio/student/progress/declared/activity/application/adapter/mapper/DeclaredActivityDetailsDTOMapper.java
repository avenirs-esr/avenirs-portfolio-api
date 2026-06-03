package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.mapper.ActivityContentDtoMapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityDetailsData;
import java.time.Instant;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {ActivityContentDtoMapper.class, FeedbackOverviewDTOMapper.class})
public interface DeclaredActivityDetailsDTOMapper {

  @Mapping(source = "declaredActivity.id", target = "id")
  @Mapping(source = "declaredActivity.activity", target = "activity")
  @Mapping(source = "declaredActivity.status", target = "status")
  @Mapping(source = "declaredActivity.reflection", target = "reflection")
  @Mapping(source = "declaredActivity.startDate", target = "startDate")
  @Mapping(source = "declaredActivity.endDate", target = "endDate")
  @Mapping(source = "declaredActivity.finishedAt", target = "finishedAt")
  @Mapping(source = "declaredActivity.createdAt", target = "createdAt")
  @Mapping(source = "declaredActivity.updatedAt", target = "updatedAt")
  DeclaredActivityDetailsDTO toDTO(DeclaredActivityDetailsData data);

  default Instant unwrap(Optional<Instant> value) {
    return value == null ? null : value.orElse(null);
  }
}
