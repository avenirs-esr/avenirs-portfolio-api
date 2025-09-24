package fr.avenirsesr.portfolio.student.progress.domain.dto;

import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;

public record SkillLevelProgressWithTraceCountDTO(
    SkillLevelProgress skillLevelProgress, int traceCount) {}
