package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.file.application.adapter.mapper.AttachmentUploadDTOMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceDetailDTO;
import fr.avenirsesr.portfolio.trace.domain.data.TraceDetailData;
import java.util.Optional;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = AttachmentUploadDTOMapper.class)
public interface TraceDetailMapper {

  TraceDetailDTO toDTO(TraceDetailData traceDetail);

  default String unwrap(Optional<String> value) {
    return value == null ? null : value.orElse(null);
  }
}
