package fr.avenirsesr.portfolio.student.progress.domain.data;

import fr.avenirsesr.portfolio.student.progress.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.trace.domain.data.TraceWithProjectNameData;
import java.util.List;

public record AdditionalSkillProgressDetails(
    AdditionalSkillProgress additionalSkillProgress,
    List<TraceWithProjectNameData> tracesWithProjectName) {}
