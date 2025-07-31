package fr.avenirsesr.portfolio.trace.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import java.util.List;

public record TraceView(List<Trace> traces, int criticalCount, PageInfo page) {}
