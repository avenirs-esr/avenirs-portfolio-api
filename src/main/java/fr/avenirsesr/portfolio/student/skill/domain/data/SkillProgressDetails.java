package fr.avenirsesr.portfolio.student.skill.domain.data;

import fr.avenirsesr.portfolio.skill.domain.data.SkillPathSegment;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.student.skill.domain.model.SkillProgress;
import java.util.List;

public record SkillProgressDetails(
    SkillProgress skillProgress, Skill skill, List<SkillPathSegment> path) {}
