package fr.avenirsesr.portfolio.additionalskill.domain.service;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillsPaginated;
import fr.avenirsesr.portfolio.additionalskill.domain.port.input.AdditionalSkillService;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.AdditionalSkillCache;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class AdditionalSkillServiceImpl implements AdditionalSkillService {
  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_PAGESIZE = 8;

  private final AdditionalSkillCache additionalSkillCache;

  @Override
  public AdditionalSkillsPaginated getAdditionalSkills(Integer page, Integer pageSize) {
    page = Optional.ofNullable(page).orElse(DEFAULT_PAGE);
    pageSize = Optional.ofNullable(pageSize).orElse(DEFAULT_PAGESIZE);
    return additionalSkillCache.findAll(page, pageSize);
  }
}
