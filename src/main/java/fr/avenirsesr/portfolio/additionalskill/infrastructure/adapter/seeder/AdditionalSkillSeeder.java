package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.additionalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillEntity;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository.AdditionalSkillDatabaseRepository;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder.fake.FakeAdditionalSkill;
import fr.avenirsesr.portfolio.interoperability.additionalskill.casoc.domain.port.input.CasocService;
import fr.avenirsesr.portfolio.interoperability.additionalskill.casol.domain.port.input.CasolService;
import fr.avenirsesr.portfolio.interoperability.additionalskill.xxi.domain.port.input.XXIService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdditionalSkillSeeder {

  private final AdditionalSkillDatabaseRepository additionalSkillDatabaseRepository;
  private final OpenSearchIndex openSearchIndex;
  private final XXIService xxiService;
  private final CasocService casocService;
  private final CasolService casolService;

  @Transactional
  public List<AdditionalSkillEntity> seed() {
    log.info("Seeding additional skill...");
    List<FakeAdditionalSkill> fakeAdditionalSkillList = FakeAdditionalSkill.of();
    List<AdditionalSkillEntity> additionalSkillEntities =
        fakeAdditionalSkillList.stream().map(FakeAdditionalSkill::toEntity).toList();

    additionalSkillDatabaseRepository.saveAllEntities(additionalSkillEntities);
    openSearchIndex.indexAll(
        additionalSkillEntities.stream().map(AdditionalSkillMapper::toDomain).toList());

    xxiService.syncSkills();
    casocService.syncSkills();
    casolService.syncSkills();

    log.info("✔ {} additionalSkills created", additionalSkillEntities.size());
    return additionalSkillEntities;
  }
}
