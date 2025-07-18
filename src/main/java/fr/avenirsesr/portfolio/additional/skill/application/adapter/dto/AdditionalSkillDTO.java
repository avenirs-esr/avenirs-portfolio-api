package fr.avenirsesr.portfolio.additional.skill.application.adapter.dto;

import java.util.List;

public record AdditionalSkillDTO(String id, String title, List<String> pathSegments, String type) {}
