package fr.avenirsesr.portfolio.additional.skill.application.adapter.response;

import fr.avenirsesr.portfolio.additional.skill.application.adapter.dto.AdditionalSkillDTO;
import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import java.util.List;

public record AdditionalSkillResponse(List<AdditionalSkillDTO> data, PageInfo page) {}
