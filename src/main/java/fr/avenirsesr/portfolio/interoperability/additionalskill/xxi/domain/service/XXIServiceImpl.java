package fr.avenirsesr.portfolio.interoperability.additionalskill.xxi.domain.service;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.additionalskill.xxi.domain.model.Category;
import fr.avenirsesr.portfolio.interoperability.additionalskill.xxi.domain.port.input.XXIService;
import java.util.ArrayList;
import java.util.List;
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
                        buildCategory(competence.category(), categories),
                        EAdditionalSkillType.XXI))
            .toList();

    additionalSkillRepository.saveAll(additionalSkills);
    openSearchIndex.indexAll(additionalSkills);
    log.info("{} Additional skills from XXI saved and indexed", additionalSkills.size());
    return additionalSkills;
  }

  private AdditionalSkillCategory buildCategory(
      Category category, ArrayList<AdditionalSkillCategory> categories) {
    var additionalSkillCategory =
        AdditionalSkillCategory.of(
            category.libelle(),
            Optional.ofNullable(category.parent())
                .map(c -> buildCategory(c, categories))
                .orElse(null),
            category.type());
    var categoryToSave =
        categories.stream()
            .filter(c -> c.uniqHash() == additionalSkillCategory.uniqHash())
            .findAny()
            .orElse(additionalSkillCategory);

    if (categoryToSave.equals(additionalSkillCategory)) {
      addCategoriesRecursively(additionalSkillCategory, categories);
    }

    return categoryToSave;
  }

  private void addCategoriesRecursively(
      AdditionalSkillCategory category, ArrayList<AdditionalSkillCategory> categories) {
    categories.add(category);
    category.getParent().ifPresent(parent -> addCategoriesRecursively(parent, categories));
  }
}
