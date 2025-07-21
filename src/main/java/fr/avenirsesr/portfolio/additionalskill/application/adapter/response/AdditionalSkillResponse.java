package fr.avenirsesr.portfolio.additionalskill.application.adapter.response;

import fr.avenirsesr.portfolio.additionalskill.application.adapter.dto.AdditionalSkillDTO;
import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(requiredProperties = {"data", "page"})
public record AdditionalSkillResponse(List<AdditionalSkillDTO> data, PageInfo page) {}
