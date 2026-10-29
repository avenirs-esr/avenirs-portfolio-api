package fr.avenirsesr.portfolio.student.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.user.application.adapter.dto.UserInfoDTO;
import java.time.Instant;

public record ReceivedAssessmentDTO(
    Integer grade,
    Integer maxGrade,
    Boolean validation,
    String comment,
    Instant requestedAt,
    Instant sentAt,
    Instant modifiedAt,
    UserInfoDTO assessedBy) {}
