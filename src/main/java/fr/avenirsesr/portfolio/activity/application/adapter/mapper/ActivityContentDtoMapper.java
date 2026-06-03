package fr.avenirsesr.portfolio.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityContentDTO;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityContentData;
import fr.avenirsesr.portfolio.file.domain.data.FileData;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;
import java.util.Optional;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActivityContentDtoMapper {
  @Mapping(target = "banner", expression = "java(mapBanner(data.banner(), baseUrl))")
  ActivityContentDTO toDTO(ActivityContentData data, @Context String baseUrl);

  default FileDTO mapBanner(FileData banner, @Context String baseUrl) {
    if (banner == null) {
      return null;
    }

    return new FileDTO(
        banner.id().orElse(null), banner.name().orElse(null), baseUrl + banner.url());
  }

  default String unwrapString(Optional<String> value) {
    return value.orElse(null);
  }

  default Integer unwrapInteger(Optional<Integer> value) {
    return value.orElse(null);
  }
}
