package fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data;

import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillCategoryDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.trace.domain.data.TraceWithProjectNameData;
import java.util.List;

public record DeclaredSkillProgressDetails(
    DeclaredSkillProgress declaredSkillProgress,
    List<TraceWithProjectNameData> tracesWithProjectName,
    List<ExternalSkillCategoryDTO> externalCategories) {}
