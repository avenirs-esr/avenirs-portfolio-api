package fr.avenirsesr.portfolio.staff.activity.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema()
public record ActivityAssessmentDTO(Boolean comment, Integer grade, Boolean validation) {}
