package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityOverviewDTO;
import fr.avenirsesr.portfolio.activity.application.adapter.dto.AuthorDTO;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityWithStudentStatusData;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActivityOverviewDtoMapper {

  @Mapping(source = "activity.id", target = "id")
  @Mapping(source = "activity.title", target = "title")
  @Mapping(source = "activity.thematic", target = "thematic")
  @Mapping(source = "activity.summary", target = "summary")
  @Mapping(source = "activity.executionPeriodInfoSummary", target = "executionPeriodInfoSummary")
  @Mapping(source = "activity.author", target = "author")
  ActivityOverviewDTO toDTO(ActivityWithStudentStatusData activityStatus);

  default String unwrap(Optional<String> value) {
    return value.orElse(null);
  }

  default AuthorDTO unwrap(Staff author) {
    return new AuthorDTO(
        author.getId(), author.getUser().getFirstName(), author.getUser().getLastName());
  }
}
