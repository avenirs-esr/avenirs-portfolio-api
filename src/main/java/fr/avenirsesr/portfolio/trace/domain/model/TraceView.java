package fr.avenirsesr.portfolio.trace.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(requiredProperties = {"traces", "criticalCount", "page"})
public record TraceView(List<Trace> traces, int criticalCount, PageInfo page) {}
