package fr.avenirsesr.portfolio.student.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.user.application.adapter.dto.UserInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(requiredProperties = {"sentAt", "assessedBy"})
public record ReceivedAssessmentDTO(
    Integer grade,
    Integer maxGrade,
    Boolean validation,
    String comment,
    Instant sentAt,
    Instant modifiedAt,
    UserInfoDTO assessedBy) {}
