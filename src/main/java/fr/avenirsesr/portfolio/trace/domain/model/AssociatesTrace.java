package fr.avenirsesr.portfolio.trace.domain.model;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.ams.domain.dto.AmsView;
import fr.avenirsesr.portfolio.student.progress.domain.dto.SkillProgressDTO;
import java.util.List;

public record AssociatesTrace(
    List<AmsView> amses,
    List<SkillProgressDTO> skillProgresses,
    List<AdditionalSkillProgress> additionalSkillProgresses) {}
