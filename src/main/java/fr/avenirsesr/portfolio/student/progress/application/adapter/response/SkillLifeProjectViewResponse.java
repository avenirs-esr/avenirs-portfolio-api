package fr.avenirsesr.portfolio.student.progress.application.adapter.response;

import fr.avenirsesr.portfolio.shared.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.shared.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillViewDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Paginated response containing Skill Life Project view information")
public record SkillLifeProjectViewResponse(List<SkillViewDTO> data, PageInfoDTO page)
    implements PagedResponse<SkillViewDTO> {}
