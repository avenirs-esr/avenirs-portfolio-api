package fr.avenirsesr.portfolio.additionalskill.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import java.util.List;

public record AdditionalSkillPagedResult(List<AdditionalSkill> content, PageInfo pageInfo) {}
