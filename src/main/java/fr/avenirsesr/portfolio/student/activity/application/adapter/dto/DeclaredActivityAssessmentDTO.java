package fr.avenirsesr.portfolio.student.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.user.application.adapter.dto.UserInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(requiredProperties = {"requestedAt"})
public record DeclaredActivityAssessmentDTO(
    String comment,
    Integer grade,
    Boolean validation,
    Instant requestedAt,
    Instant assessedAt,
    UserInfoDTO assessedBy) {}
