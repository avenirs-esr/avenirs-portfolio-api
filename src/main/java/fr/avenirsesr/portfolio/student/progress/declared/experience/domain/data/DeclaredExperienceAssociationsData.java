package fr.avenirsesr.portfolio.student.progress.declared.experience.domain.data;

import fr.avenirsesr.portfolio.trace.domain.data.TraceAssociationData;
import java.util.List;

public record DeclaredExperienceAssociationsData(List<TraceAssociationData> traceAssociations) {}
