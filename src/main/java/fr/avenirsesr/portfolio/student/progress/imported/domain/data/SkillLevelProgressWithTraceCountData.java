package fr.avenirsesr.portfolio.student.progress.imported.domain.data;

import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;

public record SkillLevelProgressWithTraceCountData(
    SkillLevelProgress skillLevelProgress, int traceCount) {}
