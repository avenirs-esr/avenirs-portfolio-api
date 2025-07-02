package fr.avenirsesr.portfolio.trace.application.adapter.response;

import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceViewDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(requiredProperties = {"traces"})
public record TracesResponse(List<TraceViewDTO> traces, int criticalCount) {}
