package fr.avenirsesr.portfolio.student.skill.domain.data;

import fr.avenirsesr.portfolio.skill.domain.data.SkillPathSegment;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import java.util.List;

public record DeclaredSkillProgressDetails(
    DeclaredSkillProgress declaredSkillProgress, Skill skill, List<SkillPathSegment> path) {}
