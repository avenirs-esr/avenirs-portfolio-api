package fr.avenirsesr.portfolio.student.skill.domain.data;

import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.student.skill.domain.model.SkillProgress;

public record SkillProgressData(
    SkillProgress skillProgress, Skill skill, SkillProgressAssociationCount associationsCount) {}
