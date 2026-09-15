package fr.avenirsesr.portfolio.student.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.user.application.adapter.dto.StudentInfoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "student", "status"})
public record StudentFeedbackItemListDTO(
    UUID feedbackId,
    StudentInfoDTO student,
    @Schema(ref = "#/components/schemas/EFeedbackStatus") EFeedbackStatus status) {}
