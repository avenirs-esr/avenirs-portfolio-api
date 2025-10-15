package fr.avenirsesr.portfolio.interoperability.additionalskill.casoc.domain.service;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillCategoryType;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.additionalskill.casoc.domain.port.input.CasocService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CasocServiceImpl implements CasocService {
  private final CompetenceReader competenceReader;
  private final OpenSearchIndex openSearchIndex;
  private final AdditionalSkillRepository additionalSkillRepository;

  @Override
  public List<AdditionalSkill> syncSkills() {
    var competences = competenceReader.readCompetences();

    var additionalSkills =
        competences.stream()
            .map(
                competence ->
                    AdditionalSkill.create(
                        competence.libelle(),
                        String.valueOf(competence.id()),
                        AdditionalSkillCategory.of(
                            competence.category().libelle(),
                            null,
                            EAdditionalSkillCategoryType.DOMAIN),
                        EAdditionalSkillType.CASOC))
            .toList();

    additionalSkillRepository.saveAll(additionalSkills);
    openSearchIndex.indexAll(additionalSkills);
    log.info("{} Additional skills from CASOC saved and indexed", additionalSkills.size());
    return additionalSkills;
  }
}
