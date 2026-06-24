package fr.avenirsesr.portfolio.shared.application.adapter.mapper;

import java.util.Optional;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OptionalMapper {

  default String unwrapString(Optional<String> value) {
    return value == null ? null : value.orElse(null);
  }

  default Integer unwrapInteger(Optional<Integer> value) {
    return value == null ? null : value.orElse(null);
  }
}
