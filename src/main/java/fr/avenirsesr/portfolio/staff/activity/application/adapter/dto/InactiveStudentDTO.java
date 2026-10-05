package fr.avenirsesr.portfolio.staff.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.user.application.adapter.dto.StudentInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(requiredProperties = {"student", "enrolledAt"})
public record InactiveStudentDTO(
    StudentInfoDTO student, Instant enrolledAt, Instant lastViewedAt) {}
