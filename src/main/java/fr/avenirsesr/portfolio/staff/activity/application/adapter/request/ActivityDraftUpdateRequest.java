package fr.avenirsesr.portfolio.staff.activity.application.adapter.request;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

@Schema()
public record ActivityDraftUpdateRequest(
    String title,
    @Schema(ref = "#/components/schemas/EActivityThematic") EActivityThematic thematic,
    String summary,
    String description,
    String recommendedCompletionContexts,
    @Schema(nullable = true) LocalDate startDate,
    @Schema(nullable = true) LocalDate endDate,
    Integer traceAllowedAssociations,
    Integer feedbackAllowedIterations,
    Boolean enableReflection,
    List<String> links) {}
