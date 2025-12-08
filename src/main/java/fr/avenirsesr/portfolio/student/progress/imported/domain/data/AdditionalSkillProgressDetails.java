package fr.avenirsesr.portfolio.student.progress.imported.domain.data;

import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillCategoryDTO;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.trace.domain.data.TraceWithProjectNameData;
import java.util.List;

public record AdditionalSkillProgressDetails(
    AdditionalSkillProgress additionalSkillProgress,
    List<TraceWithProjectNameData> tracesWithProjectName,
    List<ExternalSkillCategoryDTO> externalCategories) {}
