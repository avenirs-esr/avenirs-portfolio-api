package fr.avenirsesr.portfolio.student.progress.domain.data;

import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;

public record SkillLevelProgressWithTraceCountData(
    SkillLevelProgress skillLevelProgress, int traceCount) {}
