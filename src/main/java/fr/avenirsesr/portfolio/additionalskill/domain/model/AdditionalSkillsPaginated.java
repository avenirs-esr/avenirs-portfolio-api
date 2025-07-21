package fr.avenirsesr.portfolio.additionalskill.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(requiredProperties = {"data", "page"})
public record AdditionalSkillsPaginated(List<AdditionalSkill> data, PageInfo page) {}
