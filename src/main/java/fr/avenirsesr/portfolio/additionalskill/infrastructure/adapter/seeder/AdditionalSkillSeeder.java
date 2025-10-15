package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillEntity;
import fr.avenirsesr.portfolio.interoperability.additionalskill.casoc.domain.port.input.CasocService;
import fr.avenirsesr.portfolio.interoperability.additionalskill.casol.domain.port.input.CasolService;
import fr.avenirsesr.portfolio.interoperability.additionalskill.xxi.domain.port.input.XXIService;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdditionalSkillSeeder {
  private final XXIService xxiService;
  private final CasocService casocService;
  private final CasolService casolService;

  @Transactional
  public List<AdditionalSkillEntity> seed() {
    log.info("Seeding additional skill...");

    var xxi = xxiService.syncSkills();
    var casoc = casocService.syncSkills();
    var casol = casolService.syncSkills();

    var additionalSkills =
        Stream.of(xxi, casoc, casol)
            .flatMap(Collection::stream)
            .map(AdditionalSkillMapper::fromDomain)
            .collect(Collectors.toList());

    log.info("✔ {} additionalSkills synced", additionalSkills.size());

    return additionalSkills;
  }
}
