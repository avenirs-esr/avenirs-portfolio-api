package fr.avenirsesr.portfolio.ams.application.adapter.response;

import fr.avenirsesr.portfolio.ams.application.adapter.dto.AmsViewDTO;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.shared.application.adapter.response.PagedResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Paginated response containing AMS view information")
public record AmsViewResponse(List<AmsViewDTO> data, PageInfoDTO page)
    implements PagedResponse<AmsViewDTO> {}
