package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.additionalskill.domain.exception.AdditionalSkillNotFoundException;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.AdditionalSkillCache;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.CompetenceComplementaireDetaillee;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdditionalSkillCacheImpl implements AdditionalSkillCache {
  private final String jsonPath;
  private final ObjectMapper objectMapper;

  public AdditionalSkillCacheImpl(
      @Value("${mock.additional-skills.path:/mock/mock-additional-skills.json}") String jsonPath) {
    this.jsonPath = jsonPath;
    this.objectMapper = new ObjectMapper();
  }

  private static <T> PagedResult<T> paginate(List<T> data, PageCriteria pageCriteria) {
    int totalElements = data.size();
    int page = pageCriteria.page();
    int start = Math.min(page * pageCriteria.pageSize(), totalElements);
    int end = Math.min(start + pageCriteria.pageSize(), totalElements);
    List<T> paginatedSkills = data.subList(start, end);
    return new PagedResult<>(
        paginatedSkills, new PageInfo(pageCriteria.page(), pageCriteria.pageSize(), data.size()));
  }

  @Override
  public PagedResult<AdditionalSkill> findAll(PageCriteria pageCriteria) {
    List<AdditionalSkill> additionalSkills =
        loadAdditionalSkills().stream().map(AdditionalSkillMapper::toDomain).toList();
    return paginate(additionalSkills, pageCriteria);
  }

  @Override
  public PagedResult<AdditionalSkill> findBySkillTitle(String keyword, PageCriteria pageCriteria) {
    String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
    List<AdditionalSkill> filteredSkills = loadAdditionalSkillsByLibelle(normalizedKeyword);
    return paginate(filteredSkills, pageCriteria);
  }

  private List<CompetenceComplementaireDetaillee> loadAdditionalSkills() {
    try (InputStream is = getClass().getResourceAsStream(jsonPath)) {
      return objectMapper.readValue(is, new TypeReference<>() {});
    } catch (Exception e) {
      throw new RuntimeException("Unable to load mock additional skills", e);
    }
  }

  private List<AdditionalSkill> loadAdditionalSkillsByLibelle(String keyword) {
    return loadAdditionalSkills().stream()
        .filter(skill -> skill.libelle().toLowerCase().contains(keyword.toLowerCase()))
        .map(AdditionalSkillMapper::toDomain)
        .toList();
  }

  @Override
  public AdditionalSkill findById(UUID id) {
    return loadAdditionalSkills().stream()
        .filter(skill -> skill.id().equals(id))
        .map(AdditionalSkillMapper::toDomain)
        .findFirst()
        .orElseThrow(AdditionalSkillNotFoundException::new);
  }

  @Override
  public List<AdditionalSkill> findAllByIds(List<UUID> ids) {
    return loadAdditionalSkills().stream()
        .filter(skill -> ids.contains(skill.id()))
        .map(AdditionalSkillMapper::toDomain)
        .toList();
  }
}
