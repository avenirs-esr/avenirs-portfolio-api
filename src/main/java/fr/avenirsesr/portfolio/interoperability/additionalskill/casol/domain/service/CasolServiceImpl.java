package fr.avenirsesr.portfolio.interoperability.additionalskill.casol.domain.service;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillCategoryType;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.additionalskill.casol.domain.model.Competence;
import fr.avenirsesr.portfolio.interoperability.additionalskill.casol.domain.port.input.CasolService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CasolServiceImpl implements CasolService {
  private final CompetenceReader competenceReader;
  private final OpenSearchIndex openSearchIndex;
  private final AdditionalSkillRepository additionalSkillRepository;

  @Override
  public List<AdditionalSkill> syncSkills() {
    var competences = competenceReader.readCompetences();
    var categories = new ArrayList<AdditionalSkillCategory>();
    var additionalSkills =
        competences.stream()
            .map(
                competence ->
                    AdditionalSkill.create(
                        competence.libelle(),
                        String.valueOf(competence.id()),
                        buildCategory(competence, categories),
                        EAdditionalSkillType.CASOL))
            .toList();

    additionalSkillRepository.saveAll(additionalSkills);
    openSearchIndex.indexAll(additionalSkills);
    log.info("{} Additional skills from CASOL saved and indexed", additionalSkills.size());
    return additionalSkills;
  }

  private AdditionalSkillCategory buildCategory(
      Competence competence, ArrayList<AdditionalSkillCategory> categories) {
    var additionalSkillCategory =
        AdditionalSkillCategory.of(
            competence.category().libelle(), null, EAdditionalSkillCategoryType.DOMAIN);

    var categoryToSave =
        categories.stream()
            .filter(c -> c.uniqHash() == additionalSkillCategory.uniqHash())
            .findFirst()
            .orElse(additionalSkillCategory);

    if (categoryToSave.equals(additionalSkillCategory)) {
      categories.add(additionalSkillCategory);
    }
    return categoryToSave;
  }
}
