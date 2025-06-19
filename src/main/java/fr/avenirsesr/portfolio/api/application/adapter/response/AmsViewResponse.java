package fr.avenirsesr.portfolio.api.application.adapter.response;

import fr.avenirsesr.portfolio.api.application.adapter.dto.AmsViewDTO;
import fr.avenirsesr.portfolio.api.domain.model.PaginationInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Paginated response containing AMS view information")
public record AmsViewResponse(List<AmsViewDTO> content, PaginationInfo page)
    implements PagedResponse<AmsViewDTO> {}
