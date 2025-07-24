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
import org.springframework.stereotype.Component;

@Component
public class AdditionalSkillCacheImpl implements AdditionalSkillCache {
  private static final String JSON_PATH = "/mock/mock-additional-skills.json";
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public PagedResult<AdditionalSkill> findAll(PageCriteria pageCriteria) {
    List<AdditionalSkill> additionalSkills = loadAdditionalSkills();
    return paginate(additionalSkills, pageCriteria);
  }

  @Override
  public PagedResult<AdditionalSkill> findBySkillTitle(String keyword, PageCriteria pageCriteria) {
    String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
    List<AdditionalSkill> filteredSkills = loadAdditionalSkillsByLibelle(normalizedKeyword);
    return paginate(filteredSkills, pageCriteria);
  }

  private List<AdditionalSkill> loadAdditionalSkills() {
    try (InputStream is = getClass().getResourceAsStream(JSON_PATH)) {
      List<CompetenceComplementaireDetaillee> entities =
          objectMapper.readValue(is, new TypeReference<>() {});
      return entities.stream().map(AdditionalSkillMapper::toDomain).toList();
    } catch (Exception e) {
      throw new RuntimeException("Unable to load mock additional skills", e);
    }
  }

  private List<AdditionalSkill> loadAdditionalSkillsByLibelle(String keyword) {
    try (InputStream is = getClass().getResourceAsStream(JSON_PATH)) {
      List<CompetenceComplementaireDetaillee> entities =
          objectMapper.readValue(is, new TypeReference<>() {});
      return entities.stream()
          .filter(skill -> skill.libelle().toLowerCase().contains(keyword.toLowerCase()))
          .map(AdditionalSkillMapper::toDomain)
          .toList();
    } catch (Exception e) {
      throw new RuntimeException("Unable to load mock additional skills", e);
    }
  }

  private PagedResult<AdditionalSkill> paginate(
      List<AdditionalSkill> skills, PageCriteria pageCriteria) {
    int totalElements = skills.size();
    int start = Math.min(pageCriteria.page() * pageCriteria.pageSize(), totalElements);
    int end = Math.min(start + pageCriteria.pageSize(), totalElements);
    List<AdditionalSkill> paginatedSkills = skills.subList(start, end);
    return new PagedResult<>(
        paginatedSkills, new PageInfo(pageCriteria.page(), pageCriteria.pageSize(), skills.size()));
  }

  @Override
  public AdditionalSkill findById(UUID id) {
    try (InputStream is = getClass().getResourceAsStream(JSON_PATH)) {
      List<CompetenceComplementaireDetaillee> entities =
          objectMapper.readValue(is, new TypeReference<>() {});
      return entities.stream()
          .filter(skill -> skill.id().equals(id))
          .map(AdditionalSkillMapper::toDomain)
          .findFirst()
          .orElseThrow(AdditionalSkillNotFoundException::new);
    } catch (Exception e) {
      throw new RuntimeException("Unable to load mock additional skills", e);
    }
  }
}
