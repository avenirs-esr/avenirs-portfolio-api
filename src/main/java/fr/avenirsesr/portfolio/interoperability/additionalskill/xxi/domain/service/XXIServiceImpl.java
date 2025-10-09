package fr.avenirsesr.portfolio.interoperability.additionalskill.xxi.domain.service;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.additionalskill.xxi.domain.model.Category;
import fr.avenirsesr.portfolio.interoperability.additionalskill.xxi.domain.port.input.XXIService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class XXIServiceImpl implements XXIService {
  private final CompetenceReader competenceReader;
  private final OpenSearchIndex openSearchIndex;
  private final AdditionalSkillRepository additionalSkillRepository;

  @Override
  public void syncSkills() {
    var competences = competenceReader.readCompetences();

    var additionalSkills =
        competences.stream()
            .map(
                competence ->
                    AdditionalSkill.create(
                        competence.libelle(),
                        String.valueOf(competence.id()),
                        buildCategory(competence.category()),
                        EAdditionalSkillType.XXI))
            .toList();

    additionalSkillRepository.saveAll(additionalSkills);
    openSearchIndex.indexAll(additionalSkills);
    log.info("{} Additional skills from XXI saved and indexed", additionalSkills.size());
  }

  private AdditionalSkillCategory buildCategory(Category category) {
    return AdditionalSkillCategory.of(
        category.libelle(),
        Optional.ofNullable(category.parent()).map(this::buildCategory).orElse(null),
        category.type());
  }
}
